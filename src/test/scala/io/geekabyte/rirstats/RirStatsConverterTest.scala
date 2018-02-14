package io.geekabyte.rirstats

import io.geekabyte.rirstats.exceptions.ParseException
import io.circe.parser._

class RirStatsConverterTest extends UnitTest {

  test("Convert statistic format to JSON") {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convert(wellFormedStats.trim)
    conversionResult match {
      case Right(json) =>
        assertResult(true) {
          parse(json) === parse(wellFormedJson)
        }
      case Left(ex) =>
        fail()
    }
  }

  test("Convert extended statistic format to JSON") {
    val conversionResult: Either[List[ParseException], String] = RirStatsConverter.convertExtended(wellFormedExtendedFormat.trim)
    conversionResult match {
      case Right(json) =>
        assertResult(true) {
          parse(json) === parse(wellFormedExtendedJson)
        }
      case Left(ex) =>
        fail()
    }
  }

}
