package io.geekabyte.rirstats

import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date => JDate}

import io.geekabyte.rirstats.exceptions.{InvalidValue, ParseException}
import io.geekabyte.rirstats.models.{HeaderLine, RecordCount, RecordEntry, RecordLine, Registry, Resource, RirStat, RirStatMeta, RirStatRecord, RirStatRecordEntry, RirStatResourceCount, RirStatSource, RirUtcOffset, SerialNumber, SummaryLine, afrinic, apnic, arin, asn, iana, ipv4, ipv6, lacnic, ripencc}

import scala.util.Try


object ConvertHelpers {
  import ImplicitOps._

  val fromCsvToModel: (Option[URL], HeaderLine, Seq[SummaryLine], Seq[RecordLine]) => RirStat =
    (source:Option[URL], headerLine: HeaderLine, summaryLines: Seq[SummaryLine], recordLines: Seq[RecordLine]) => {

      val asnCount : Int = summaryLines.filter(_.resourceType == asn).map(_.count).sum
      val ipv4Count: Int = summaryLines.filter(_.resourceType == ipv4).map(_.count).sum
      val ipv6Count: Int = summaryLines.filter(_.resourceType == ipv6).map(_.count).sum

      val records: Seq[RirStatRecordEntry] = recordLines.map((line: RecordLine) => {
        val rirStatRecord = RirStatRecord(
          line.resourceType,
          line.resource.firstAddress,
          line.resource.prefix,
          line.resource.count
        )

        RirStatRecordEntry(line.countryCode, line.date, line.resourceStatus, rirStatRecord, line.opaqueId)
      })

      val registryToRecordLines: Map[Registry, Seq[RecordLine]] = recordLines.groupBy(_.registry)

      val registryToRecords: Map[Registry, Seq[RirStatRecordEntry]] = registryToRecordLines.mapValues(recordLines => recordLines.map((line: RecordLine) => {
        val rirStatRecord = RirStatRecord(
          line.resourceType,
          line.resource.firstAddress,
          line.resource.prefix,
          line.resource.count
        )

        RirStatRecordEntry(line.countryCode, line.date, line.resourceStatus, rirStatRecord, line.opaqueId)
      })).withDefaultValue(List())

      source match {
        case Some(statSource) =>
          val sourceUrl: String = statSource.toString
          RirStat(Some(RirStatMeta(source = Seq(RirStatSource(sourceUrl, s"$sourceUrl.md5")))),
            headerLine.version,
            headerLine.registry,
            SerialNumber(headerLine.serial),
            headerLine.recordCount,
            headerLine.startDate,
            headerLine.endDate,
            RirUtcOffset(headerLine.utcOffset),
            RirStatResourceCount(asn = asnCount, ipv4 = ipv4Count, ipv6 = ipv6Count),
            RecordEntry(
              afrinic=registryToRecords(afrinic).headOption.map(_ => registryToRecords(afrinic)),
              apnic=registryToRecords(apnic).headOption.map(_ => registryToRecords(apnic)),
              arin=registryToRecords(arin).headOption.map(_ => registryToRecords(arin)),
              iana=registryToRecords(iana).headOption.map(_ => registryToRecords(iana)),
              lacnic=registryToRecords(lacnic).headOption.map(_ => registryToRecords(lacnic)),
              ripencc=registryToRecords(ripencc).headOption.map(_ => registryToRecords(ripencc))
            )
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
            RecordEntry(
              afrinic=registryToRecords(afrinic).headOption.map(_ => registryToRecords(afrinic)),
              apnic=registryToRecords(apnic).headOption.map(_ => registryToRecords(apnic)),
              arin=registryToRecords(arin).headOption.map(_ => registryToRecords(arin)),
              iana=registryToRecords(iana).headOption.map(_ => registryToRecords(iana)),
              lacnic=registryToRecords(lacnic).headOption.map(_ => registryToRecords(lacnic)),
              ripencc=registryToRecords(ripencc).headOption.map(_ => registryToRecords(ripencc))
            )
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
    /**
      * ripencc|*|ipv4|*|65367|summary
      * 0      |*|  2 |*|  4  | 5
      */
    val components: Array[String] = summaryLine.split('|')
    for {
      registry <- components(0).toRegistry
      resourceType <- components(2).toResourceType
      count <- Try(components(4).toInt).fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), count => Right(count))
    } yield {
      SummaryLine(registry, resourceType, count)
    }
  }

  val fromRecordLine: String => Either[ParseException, RecordLine] = (recordLine: String) => {
    /**
      * ripencc|EU|ipv6|2001:600::|32|19990826|allocated|647c2f10-dda2-4809-88e8-49024f31ad17
      *   0    |1 | 2  |     3    |4 |    5   |  6      | 7 (optional opaque id)
      *   or
      * ripencc||ipv6|2001:601::|32||reserved
      *     0  |1| 2 |     3    |4|5|  6
      */
    val components: Array[String] = recordLine.split('|')
    for {
      registry <- components(0).toRegistry
      resourceType <- components(2).toResourceType
      resourceStatus <- components(6).toResourceStatus

      countryCode <- Try(if (components(1).toString.equals("")) None else Some(components(1).toString))
        .fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: Option[String]))

      firstAddress <- Try(components(3).toString)
        .fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: String))

      count <- Try {
        resourceType match {
          case `ipv4` | `asn` => components(4).toInt
          case `ipv6` => scala.math.pow(2, 128 - components(4).toInt)
        }
      }.fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: Double))

      optionalPrefix <- Try {
        resourceType match {
          case `ipv6` => Some(s"/${components(4).toInt}")
          case _ => None
        }
      }.fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: Option[String]))

      dateValue <- Try(components(5).toString)
        .fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: String))

      assignedOrAllocatedDate <- Try(if (dateValue.equals("")) None else Some(formatter.parse(dateValue)))
        .fold((ex:Throwable) => Left(InvalidValue(ex, ex.getMessage)), Right(_: Option[JDate]))

      opaqueId <- Try(Some(components(7).toString)).fold({
        case ex:IndexOutOfBoundsException => Right(None)
        case ex => Left(InvalidValue(ex, ex.getMessage))
      }, Right(_: Some[String]))

    } yield {
      val resource = Resource(resourceType, firstAddress, count, optionalPrefix)
      RecordLine(registry, countryCode, resourceType, resourceStatus, assignedOrAllocatedDate, resource, opaqueId)
    }
  }
}
