package io.geekabyte.rirstats

import io.geekabyte.rirstats.exceptions.{InvalidValue, ParseException, UnknownRegistryException}
import io.circe.parser._

class RirStatsConverterTest extends UnitTest {

  "A rir stat exchange string" should "be converted to JSON" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convert(well_formed_stats.trim)

    conversionResult match {
      case Right(convertedJson) => assert(parse(convertedJson) === parse(well_formed_json))
      case Left(exceptions) => fail(exceptions.mkString)
    }
  }

  "An extended rir stat exchange string" should "be converted to JSON" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(well_formed_extended_format.trim)

    conversionResult match {
      case Right(convertedJson) => assert(parse(convertedJson) === parse(well_formed_extended_json))
      case Left(exceptions) => fail(exceptions.mkString)
    }
  }

  "An rir stat with invalid version" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_version.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid registry value" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_registry.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid serial number" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_serial_number.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid resource count" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_resource_count.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid start date" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_start_date.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid end date" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_end_date.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }
  "An rir stat with invalid time zone" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_header.stats_with_invalid_time_zone.trim)
    conversionResult match {
      case Left(exceptions) => succeed
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid registry value in summary" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_summary.invalid_registry_line)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid ip type value in summary" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_summary.invalid_ip_type_line)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid count value in summary" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_summary.invalid_count_line)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid registry value in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_registry_line)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid country code value in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_country_code)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid ip type in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_ip_type)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid ip value in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_ip_value)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid ip count in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_count_value)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid date in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_date_value)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

  "An rir stat with invalid status in record" should "result in an exception" in {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(stats_with_invalid_records.invalid_status_value)
    conversionResult match {
      case Left(exceptions) => exceptions.exists(_.isInstanceOf[InvalidValue])
      case Right(_) => fail("exception expected")
    }
  }

}
