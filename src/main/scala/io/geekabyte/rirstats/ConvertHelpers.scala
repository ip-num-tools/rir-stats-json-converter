package io.geekabyte.rirstats

import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date => JDate}

import io.geekabyte.rirstats.exceptions.{InvalidValue, ParseException}
import io.geekabyte.rirstats.models.{HeaderLine, RecordCount, RecordLine, Resource, RirStat, RirStatMeta, RirStatRecord, RirStatRecordEntry, RirStatResourceCount, RirStatSource, RirUtcOffset, SerialNumber, SummaryLine, asn, ipv4, ipv6}

import scala.util.Try


object ConvertHelpers {
  import ImplicitOps._

  val fromCsvToModel: (Option[URL], HeaderLine, Seq[SummaryLine], Seq[RecordLine]) => RirStat =
    (source:Option[URL], headerLine: HeaderLine, summaryLines: Seq[SummaryLine], recordLines: Seq[RecordLine]) => {

      val asnCount: Int = summaryLines.foldLeft(0)((result: Int, line: SummaryLine) => {
        if (line.resourceType == asn) {result + line.count} else result
      })
      val ipv4Count: Int = summaryLines.foldLeft(0)((result: Int, line: SummaryLine) => {
        if (line.resourceType == ipv4) {result + line.count} else result
      })
      val ipv6Count: Int = summaryLines.foldLeft(0)((result: Int, line: SummaryLine) => {
        if (line.resourceType == ipv6) {result + line.count} else result
      })

      val records: Seq[RirStatRecordEntry] = recordLines.map((line: RecordLine) => {
        val rirStatRecord = RirStatRecord(
          line.resourceType,
          line.resource.firstAddress,
          line.resource.prefix,
          line.resource.count
        )

        RirStatRecordEntry(line.registry, line.countryCode, line.date, line.resourceStatus, rirStatRecord, line.opaqueId)
      })

      source match {
        case Some(statSource) =>
          val sourceUrl: String = statSource.toString
          RirStat(Some(RirStatMeta(source = Seq(RirStatSource(sourceUrl, s"${sourceUrl}.md5")))),
            headerLine.version,
            headerLine.registry,
            SerialNumber(headerLine.serial),
            headerLine.recordCount,
            headerLine.startDate,
            headerLine.endDate,
            RirUtcOffset(headerLine.utcOffset),
            RirStatResourceCount(asn = asnCount, ipv4 = ipv4Count, ipv6 = ipv6Count),
            records
          )
        case None =>
          RirStat(None,
            headerLine.version,
            headerLine.registry,
            SerialNumber(headerLine.serial),
            headerLine.recordCount,
            headerLine.startDate,
            headerLine.endDate,
            RirUtcOffset(headerLine.utcOffset),
            RirStatResourceCount(asn = asnCount, ipv4 = ipv4Count, ipv6 = ipv6Count),
            records
          )
      }
    }

  private val formatter = new SimpleDateFormat("yyyyMMdd")
  val fromHeaderLine: String => Either[ParseException, HeaderLine] = (headerLine:String) => {
    /**
      * 2|ripencc|1515711599|113840|19830705|20180111|+0100
      * 0|  1    |     2    |   3  |   4    |   5    |   6
      */
    val components: Array[String] = headerLine.split('|')
    for {
      version <- Try(components(0).toDouble).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), (version: Double) => Right(version))
      registry <- components(1).toRegistry
      serial <- Try(components(2).toInt).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), serial => Right(serial))
      recordCount <- Try(components(3).toInt).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), rcount => Right(rcount))
      startDate <- Try(formatter.parse(components(4).toString)).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), formattedDate => Right(formattedDate))
      endDate <- Try(formatter.parse(components(5).toString)).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), formattedDate => Right(formattedDate))
      utcOffset <- Try(components(6).toString).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), utc => Right(utc))
    } yield {
      HeaderLine(version,registry,serial,RecordCount(recordCount),startDate,endDate,utcOffset)
    }
  }

  val fromSummaryLine: String => Either[ParseException, SummaryLine] = (summaryLine:String) => {
    val components: Array[String] = summaryLine.split('|')
    for {
      registry <- components(0).toRegistry
      resourceType <- components(2).toResourceType
    } yield {
      val count: Int = components(4).toInt
      models.SummaryLine(registry, resourceType, count)
    }
  }

  val fromRecordLine: String => Either[ParseException, RecordLine] = (recordLine: String) => {
    val components: Array[String] = recordLine.split('|')
    for {
      registry <- components(0).toRegistry
      resourceType <- components(2).toResourceType
      resourceStatus <- components(6).toResourceStatus
    } yield {
      val countryCode: Option[String] = if (components(1).toString.equals("")) None else Some(components(1).toString)
      val firstAddress: String = components(3).toString

      val count: Double = resourceType match {
        case `ipv4` | `asn` => components(4).toInt
        case `ipv6` => scala.math.pow(2, 128 - components(4).toInt)
      }

      val optionalPrefix: Option[String] = resourceType match {
        case `ipv6` => Some(s"/${components(4).toInt}")
        case _ => None
      }

      val assignedOrAllocatedDate: Option[JDate] = Try(Some(formatter.parse(components(5).toString))).getOrElse(None)

      val opaqueId = Try(Some(components(7).toString)).getOrElse(None)
      val resource = Resource(resourceType, firstAddress, count, optionalPrefix)
      RecordLine(registry, countryCode, resourceType, resourceStatus, assignedOrAllocatedDate, resource, opaqueId)
    }
  }
}
