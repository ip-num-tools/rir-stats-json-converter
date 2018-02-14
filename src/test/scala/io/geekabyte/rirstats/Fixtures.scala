package io.geekabyte.rirstats

trait Fixtures {

  def wellFormedStats =
    """
      |2|ripencc|1515711599|113840|19830705|20180111|+0100
      |ripencc|*|ipv4|*|65367|summary
      |ripencc|*|asn|*|32265|summary
      |ripencc|*|ipv6|*|16208|summary
      |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
      |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
      |ripencc|EU|asn|137|1|19930901|allocated
    """.stripMargin

  def wellFormedJson =
    """
      |{
      |   "version":2.0,
      |   "registry":"ripencc",
      |   "serial_number":1515711599,
      |   "record_count":113840,
      |   "start_date":"19830705",
      |   "end_date":"20180111",
      |   "rir_utc_offset":"+0100",
      |   "resource_count":{
      |      "asn":32265,
      |      "ipv4":65367,
      |      "ipv6":16208
      |   },
      |   "records":[
      |      {
      |         "registry":"ripencc",
      |         "country_code":"FR",
      |         "date":"20100712",
      |         "status":"allocated",
      |         "resource":{
      |            "type":"ipv4",
      |            "first_address":"2.0.0.0",
      |            "count":1048576.0
      |         }
      |      },
      |      {
      |         "registry":"ripencc",
      |         "country_code":"EU",
      |         "date":"19990826",
      |         "status":"allocated",
      |         "resource":{
      |            "type":"ipv6",
      |            "first_address":"2001:600::",
      |            "prefix":"/32",
      |            "count":7.922816251426434E28
      |         }
      |      },
      |      {
      |         "registry":"ripencc",
      |         "country_code":"EU",
      |         "date":"19930901",
      |         "status":"allocated",
      |         "resource":{
      |            "type":"asn",
      |            "first_address":"137",
      |            "count":1.0
      |         }
      |      }
      |   ]
      |}
    """.stripMargin

  def wellFormedExtendedFormat =
  """
    |2|ripencc|1514933999|176081|19830705|20180102|+0100
    |ripencc|*|ipv4|*|68487|summary
    |ripencc|*|asn|*|36376|summary
    |ripencc|*|ipv6|*|71218|summary
    |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated|647c2f10-dda2-4809-88e8-49024f31ad17
    |ripencc||ipv6|2001:601::|32||reserved
    |ripencc||ipv6|2001:674::|30||reserved
    |ripencc||ipv6|2001:678:1d::|48||reserved
  """.stripMargin

  def wellFormedExtendedJson =
  """
    |{
    |   "version":2.0,
    |   "registry":"ripencc",
    |   "serial_number":1514933999,
    |   "record_count":176081,
    |   "start_date":"19830705",
    |   "end_date":"20180102",
    |   "rir_utc_offset":"+0100",
    |   "resource_count":{
    |      "asn":36376,
    |      "ipv4":68487,
    |      "ipv6":71218
    |   },
    |   "records":[
    |      {
    |         "registry":"ripencc",
    |         "country_code":"FR",
    |         "date":"20100712",
    |         "status":"allocated",
    |         "resource":{
    |            "type":"ipv4",
    |            "first_address":"2.0.0.0",
    |            "count":1048576.0
    |         },
    |         "opaqueId":"647c2f10-dda2-4809-88e8-49024f31ad17"
    |      },
    |      {
    |         "registry":"ripencc",
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:601::",
    |            "prefix":"/32",
    |            "count":7.922816251426434E28
    |         }
    |      },
    |      {
    |         "registry":"ripencc",
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:674::",
    |            "prefix":"/30",
    |            "count":3.1691265005705735E29
    |         }
    |      },
    |      {
    |         "registry":"ripencc",
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:678:1d::",
    |            "prefix":"/48",
    |            "count":1.2089258196146292E24
    |         }
    |      }
    |   ]
    |}
  """.stripMargin

}
