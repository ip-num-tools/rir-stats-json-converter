package io.geekabyte.rirstats.models

import java.util.{Date => JDate}

case class HeaderLine(version:Double,
                      registry: Registry,
                      serial:Int,
                      recordCount:RecordCount,
                      startDate: JDate,
                      endDate:JDate,
                      utcOffset:String)

case class SummaryLine(registry: Registry, resourceType: ResourceType, count:Int)
case class RecordLine(registry: Registry,
                      countryCode: Option[String],
                      resourceType: ResourceType,
                      resourceStatus: ResourceStatus,
                      date: Option[JDate],
                      resource:Resource,
                      opaqueId: Option[String])

case class ResourceCount(asn:Int, ipv4:Int, ipv6:Int)
case class Resource(resourceType: ResourceType, firstAddress: String, count: Double, prefix: Option[String])

sealed trait Registry
case object afrinic extends Registry
case object apnic extends Registry
case object arin extends Registry
case object iana extends Registry
case object lacnic extends Registry
case object ripencc extends Registry

sealed trait ResourceType
case object asn extends ResourceType
case object ipv4 extends ResourceType
case object ipv6 extends ResourceType

sealed trait ResourceStatus
case object allocated extends ResourceStatus
case object assigned extends ResourceStatus
case object available extends ResourceStatus
case object reserved extends ResourceStatus

case class RirStatSource(url:String, signature:String)
case class RirStatMeta(source:Seq[RirStatSource])
case class RirStatResourceCount(asn:Int, ipv4:Int, ipv6:Int)

case class RirStatRecord(`type`: ResourceType,
                         first_address: String,
                         prefix: Option[String],
                         count: Double)

case class RirStatRecordEntry(country_code: Option[String],
                              date: Option[JDate],
                              status: ResourceStatus,
                              resource: RirStatRecord,
                              opaqueId: Option[String])

case class RecordEntry(afrinic: Option[Seq[RirStatRecordEntry]],
                       apnic: Option[Seq[RirStatRecordEntry]],
                       arin: Option[Seq[RirStatRecordEntry]],
                       iana: Option[Seq[RirStatRecordEntry]],
                       lacnic: Option[Seq[RirStatRecordEntry]],
                       ripencc: Option[Seq[RirStatRecordEntry]])

case class RirStat(meta:Option[RirStatMeta],
                   version:Double,
                   registry: Registry,
                   serial_number: SerialNumber,
                   record_count: RecordCount,
                   start_date: JDate,
                   end_date: JDate,
                   rir_utc_offset: RirUtcOffset,
                   resource_count: RirStatResourceCount,
                   records: RecordEntry)

case class SerialNumber(serialNumber:Int) extends AnyVal
case class RecordCount(recordCount:Int) extends AnyVal
case class RirUtcOffset(rirUtcOffset: String) extends AnyVal