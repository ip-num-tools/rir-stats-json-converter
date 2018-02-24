package io.geekabyte.rirstats


import java.net.URL
import io.circe.generic.auto._
import io.circe.syntax._
import io.geekabyte.rirstats.ConvertHelpers._
import io.geekabyte.rirstats.exceptions.{HeaderLineNotFoundException, ParseException, RIRStateJsonConversionException}
import io.geekabyte.rirstats.models.{HeaderLine, RecordLine, RirStat, SummaryLine}


object RirStatsConverter {

  private val summaryLineRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|\*\|)(asn|ipv4|ipv6)(\|\*\|)(\d+)(\|)(summary)"""

  private val recordLineRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|)([A-Z]{0,2})(\|)(asn|ipv4|ipv6)(\|)((\w(\.|:{1,2})?)+)(\|)(\d+)(\|)(\d*)(\|)(assigned|allocated)""".stripMargin

  private val recordLineExtendedRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|)([A-Z]{0,2})(\|)(asn|ipv4|ipv6)(\|)((\w(\.|:{1,2})?)+)(\|)(\d+)(\|)(\d*)(\|)(assigned|allocated|reserved|available)(\|*)(([0-9a-fA-F](\-)?)*)""".stripMargin

  private val isSummaryLine:String => Boolean = (lines:String) => lines.matches(summaryLineRegex)
  private val isRecordLine:String => Boolean = (lines:String) => lines.matches(recordLineRegex)
  private val isExtendedRecordLine:String => Boolean = (lines:String) => lines.matches(recordLineExtendedRegex)

  def convert(content:String): Either[List[ParseException], String] = {
    val lines: List[String] = content.split(System.lineSeparator).toList
    applyConversion(lines, None, isRecordLine, validator)
  }

  def convert(source:URL): Either[List[ParseException], String] = {
    val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
    applyConversion(lines, Some(source), isRecordLine, validator)
  }

  def convertExtended(source:URL): Either[List[ParseException], String] = {
    val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
    applyConversion(lines, Some(source), isExtendedRecordLine, extendedValidator)
  }

  def convertExtended(content:String): Either[List[ParseException], String] = {
    val lines: List[String] = content.split(System.lineSeparator).toList
    applyConversion(lines, None, isExtendedRecordLine, extendedValidator)
  }

  private def applyConversion(lines: List[String],
                              source: Option[URL],
                              recordLineChecker: (String => Boolean),
                              validator: RIRStatValidator): Either[List[ParseException], String] = {
    val headerLine: Either[List[ParseException], HeaderLine] = lines.headOption match {
      case Some(header) => fromHeaderLine(header) match {
        case Right(line) => Right(line)
        case Left(er) => Left(List(er))
      }
      case None => Left(List(HeaderLineNotFoundException))
    }

    val summaryLines: Either[List[ParseException], List[SummaryLine]] = lines.collect({ case x if isSummaryLine(x) => x })
      .map(fromSummaryLine(_: String)).partition(_.isLeft) match {
      case (Nil, ints) => Right(for (Right(i) <- ints) yield i)
      case (strings, _) => Left(for (Left(s) <- strings) yield s)
    }

    val recordLines: Either[List[ParseException], List[RecordLine]] = lines.collect({ case x if recordLineChecker(x) => x })
      .map(fromRecordLine(_: String)).partition(_.isLeft) match {
      case (Nil, ints) => Right(for (Right(i) <- ints) yield i)
      case (strings, _) => Left(for (Left(s) <- strings) yield s)
    }

    val rirStat: Either[List[ParseException], RirStat] = for {
      header <- headerLine
      summaries <- summaryLines
      records <- recordLines
    } yield {
      fromCsvToModel(source, header, summaries, records)
    }

    rirStat.flatMap((stat: RirStat) => {
      import io.circe.Printer
      import io.geekabyte.rirstats.JsonEncoders._
      val printer = Printer.noSpaces.copy(dropNullValues = true)
      val statAsJsonString: String = printer.pretty(stat.asJson)
      validator.validate(statAsJsonString)
    })
  }
}