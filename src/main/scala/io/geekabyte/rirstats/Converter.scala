package io.geekabyte.rirstats


import java.net.URL

import cats.data.Validated
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.geekabyte.rirstats.ConvertHelpers._
import io.geekabyte.rirstats.exceptions.{HeaderLineNotFoundException, InvalidLine, ParseException, RIRStateJsonConversionException}
import io.geekabyte.rirstats.models.{HeaderLine, RecordLine, RirStat, SummaryLine}
import shapeless.record

import scala.concurrent.{ExecutionContext, Future}
import scala.io.BufferedSource


object RirStatsConverter {

  private val summaryLineRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|\*\|)(asn|ipv4|ipv6)(\|\*\|)(\d+)(\|)(summary)""".stripMargin

  private val recordLineRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|)([A-Z]{0,2})(\|)(asn|ipv4|ipv6)(\|)((\w(\.|:{1,2})?)+)(\|)(\d+)(\|)(\d*)(\|)(assigned|allocated)""".stripMargin

  private val recordLineExtendedRegex =
    """(afrinic|apnic|arin|iana|lacnic|ripencc)(\|)([A-Z]{0,2})(\|)(asn|ipv4|ipv6)(\|)((\w(\.|:{1,2})?)+)(\|)(\d+)(\|)(\d*)(\|)(assigned|allocated|reserved|available)(\|*)(([0-9a-fA-F](\-)?)*)""".stripMargin

  private val isSummaryLine:String => Boolean = (lines:String) => lines.matches(summaryLineRegex)
  private val isRecordLine:String => Boolean = (lines:String) => lines.matches(recordLineRegex)
  private val isExtendedRecordLine:String => Boolean = (lines:String) => lines.matches(recordLineExtendedRegex)

  def validate(stat: RirStat, validator: RIRStatValidator): Validated[List[ParseException], String] = {
      import io.circe.Printer
      import io.geekabyte.rirstats.JsonEncoders._
      val printer = Printer.noSpaces.copy(dropNullValues = true)
      val statAsJsonString: String = printer.pretty(stat.asJson)
      validator.validate(statAsJsonString)
  }

  def convert(content:String): Either[List[ParseException], String] = {
    val lines: List[String] = content.split(System.lineSeparator).toList
    for {
      rirStat <- convertToModel(lines, None, isRecordLine).toEither
      result <- validate(rirStat, validator).toEither
    } yield result
  }

  def convert(source:URL): Either[List[ParseException], String] = {
    val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
    for {
      rirStat <- convertToModel(lines, Some(source), isRecordLine).toEither
      result <- validate(rirStat, validator).toEither
    } yield result
  }

//  def convert(sources:Seq[URL])(implicit executionContext: ExecutionContext): Either[List[ParseException], String] = {
//    ???
////    val futureRirStats: Seq[Future[Either[List[ParseException], RirStat]]] = sources.map(source => {
////      val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
////      Future {convertToModel(lines, Some(source), isRecordLine)}
////    })
////
////    val eventualSeq: Future[Seq[Either[List[ParseException], RirStat]]] = Future.sequence(futureRirStats)
////
////    val evetualRiRStats: Future[Either[Seq[List[ParseException]], Seq[RirStat]]] = eventualSeq.map((values: Seq[Either[List[ParseException], RirStat]]) => {
////      values .partition(_.isLeft) match {
////        case (Nil, ints) => Right(for (Right(i) <- ints) yield i)
////        case (strings, _) => Left(for (Left(s) <- strings) yield s)
////      }
////    })
////    val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
////    for {
////      rirStat <- convertToModel(lines, Some(source), isRecordLine)
////      result <- validate(rirStat, validator)
////    } yield result
//  }

  def convertExtended(source:URL): Either[List[ParseException], String] = {
    val lines: List[String] = scala.io.Source.fromURL(source).getLines().toList
    for {
      rirStat <- convertToModel(lines, Some(source), isExtendedRecordLine).toEither
      result <- validate(rirStat, extendedValidator).toEither
    } yield result
  }

  def convertExtended(content:String): Either[List[ParseException], String] = {
    val lines: List[String] = content.split(System.lineSeparator).toList
    for {
      rirStat <- convertToModel(lines, None, isExtendedRecordLine).toEither
      result <- validate(rirStat, extendedValidator).toEither
    } yield result
  }

  private def convertToModel(lines: List[String],
                              source: Option[URL],
                              recordLineChecker: (String => Boolean)): Validated[List[ParseException], RirStat] = {
    val headerLine: Validated[List[ParseException], HeaderLine] = lines.headOption match {
      case Some(header) => fromHeaderLine(header)
      case None => Validated.invalid(List(HeaderLineNotFoundException()))
    }

    val summaryLines: Validated[List[ParseException], List[SummaryLine]] = lines
        .collect({ case x if isSummaryLine(x) => x })
        .traverse(fromSummaryLine)


    val recordLines: Validated[List[ParseException], List[RecordLine]] = lines
        .collect({ case x if recordLineChecker(x) => x })
        .traverse(fromRecordLine)


    val nonValidLines = lines.drop(1).filterNot((line) => {
      isSummaryLine(line) || recordLineChecker(line)
    })

    if (nonValidLines.nonEmpty) {
      List(InvalidLine(nonValidLines)).invalid[RirStat]
    } else {
      (headerLine, summaryLines, recordLines).mapN((header,summary, record) => fromCsvToModel(source, header,summary,record))
    }
  }
}