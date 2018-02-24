package io.geekabyte.rirstats

import io.geekabyte.rirstats.models.{allocated, asn, ipv4, ipv6, ripencc}

trait Fixtures {

  def well_formed_stats =
    """
      |2|ripencc|1515711599|113840|19830705|20180111|+0100
      |ripencc|*|ipv4|*|65367|summary
      |ripencc|*|asn|*|32265|summary
      |ripencc|*|ipv6|*|16208|summary
      |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
      |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
      |ripencc|EU|asn|137|1|19930901|allocated
    """.stripMargin

  def well_formed_extended_format =
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

  def well_formed_json =
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
      |  "records" : {
      |    "ripencc" : [
      |      {
      |        "country_code" : "FR",
      |        "date" : "20100712",
      |        "status" : "allocated",
      |        "resource" : {
      |          "type" : "ipv4",
      |          "first_address" : "2.0.0.0",
      |          "count" : 1048576.0
      |        }
      |      },
      |      {
      |        "country_code" : "EU",
      |        "date" : "19990826",
      |        "status" : "allocated",
      |        "resource" : {
      |          "type" : "ipv6",
      |          "first_address" : "2001:600::",
      |          "prefix" : "/32",
      |          "count" : 7.922816251426434E28
      |        }
      |      },
      |      {
      |        "country_code" : "EU",
      |        "date" : "19930901",
      |        "status" : "allocated",
      |        "resource" : {
      |          "type" : "asn",
      |          "first_address" : "137",
      |          "count" : 1.0
      |        }
      |      }
      |    ]
      |  }
      |}
    """.stripMargin

  def well_formed_extended_json =
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
    |   "records":{
    |     "ripencc": [{
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
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:601::",
    |            "prefix":"/32",
    |            "count":7.922816251426434E28
    |         }
    |      },
    |      {
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:674::",
    |            "prefix":"/30",
    |            "count":3.1691265005705735E29
    |         }
    |      },
    |      {
    |         "status":"reserved",
    |         "resource":{
    |            "type":"ipv6",
    |            "first_address":"2001:678:1d::",
    |            "prefix":"/48",
    |            "count":1.2089258196146292E24
    |         }
    |      }]
    |   }
    |}
  """.stripMargin


  object stats_with_invalid_header {

    def stats_with_invalid_version =
      """
        |not_number|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_registry =
      """
        |2|wrong|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_serial_number =
      """
        |2|ripencc|not_a_valid_serial|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_resource_count =
      """
        |2|ripencc|1515711599|not_valid_resource|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_start_date =
      """
        |2|ripencc|1515711599|113840|not_valid|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_end_date =
      """
        |2|ripencc|1515711599|113840|19830705|not_valid|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def stats_with_invalid_time_zone =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100_invalid
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

  }
  object stats_with_invalid_records {
    /**
      *    """
      |2|ripencc|1515711599|113840|19830705|20180111|+0100
      |ripencc|*|ipv4|*|65367|summary
      |ripencc|*|asn|*|32265|summary
      |ripencc|*|ipv6|*|16208|summary
      |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
      |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
      |ripencc|EU|asn|137|1|19930901|allocated
    """.stripMargin
      */
    def invalid_registry_line =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc_invalid|FR|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_country_code =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR_not_valid|ipv4|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_ip_type =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4_not_valid|2.0.0.0|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_ip_value =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0.0.2345|1048576|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_count_value =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0.0|1048576_invalid|20100712|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_date_value =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0.0|1048576|20100712_invalid|allocated
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin

    def invalid_status_value =
      """
        |2|ripencc|1515711599|113840|19830705|20180111|+0100
        |ripencc|*|ipv4|*|65367|summary
        |ripencc|*|asn|*|32265|summary
        |ripencc|*|ipv6|*|16208|summary
        |ripencc|FR|ipv4|2.0.0.0.0|1048576|20100712|allocated_invalid
        |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
        |ripencc|EU|asn|137|1|19930901|allocated
      """.stripMargin
  }

  object stats_with_invalid_summary {
    def invalid_registry_line =   """
                                    |2|ripencc|1515711599|113840|19830705|20180111|+0100
                                    |ripencc_not_a_registry|*|ipv4|*|65367|summary
                                    |ripencc|*|asn|*|32265|summary
                                    |ripencc|*|ipv6|*|16208|summary
                                    |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
                                    |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
                                    |ripencc|EU|asn|137|1|19930901|allocated
                                  """.stripMargin
    def invalid_ip_type_line =   """
                                    |2|ripencc|1515711599|113840|19830705|20180111|+0100
                                    |ripencc|*|ipv4_not_valid|*|65367|summary
                                    |ripencc|*|asn|*|32265|summary
                                    |ripencc|*|ipv6|*|16208|summary
                                    |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
                                    |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
                                    |ripencc|EU|asn|137|1|19930901|allocated
                                  """.stripMargin

    def invalid_count_line =   """
                                   |2|ripencc|1515711599|113840|19830705|20180111|+0100
                                   |ripencc|*|ipv4|*|65367_not_valid|summary
                                   |ripencc|*|asn|*|32265|summary
                                   |ripencc|*|ipv6|*|16208|summary
                                   |ripencc|FR|ipv4|2.0.0.0|1048576|20100712|allocated
                                   |ripencc|EU|ipv6|2001:600::|32|19990826|allocated
                                   |ripencc|EU|asn|137|1|19930901|allocated
                                 """.stripMargin
  }

}
