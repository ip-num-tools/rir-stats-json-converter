package io.geekabyte.rirstats

import io.geekabyte.rirstats.exceptions.{ParseException, UnknownRegistryException, UnknownResourceException, UnknownResourceStatusException}
import io.geekabyte.rirstats.models.{Registry, ResourceStatus, ResourceType, afrinic, allocated, apnic, arin, asn, assigned, available, iana, ipv4, ipv6, lacnic, reserved, ripencc}

object ImplicitOps {
  implicit class StringRegistry(arg:String) {
    def toRegistry: Either[ParseException, Registry] = {
      if (arg == "afrinic") {
        Right(afrinic)
      } else if(arg == "apnic") {
        Right(apnic)
      } else if (arg == "arin") {
        Right(arin)
      } else if (arg == "iana") {
        Right(iana)
      } else if (arg == "lacnic") {
        Right(lacnic)
      } else if (arg == "ripencc") {
        Right(ripencc)
      } else {
        Left(UnknownRegistryException)
      }
    }
  }
  implicit class StringResourceType(arg: String) {
    def toResourceType: Either[ParseException, ResourceType] = {
      if (arg == "asn") {
        Right(asn)
      } else if (arg == "ipv4") {
        Right(ipv4)
      } else if (arg == "ipv6") {
        Right(ipv6)
      } else {
        Left(UnknownResourceException)
      }
    }
  }
  implicit class StringResourceStatus(arg: String) {
    def toResourceStatus: Either[ParseException, ResourceStatus] = {
      if (arg == "allocated") {
        Right(allocated)
      } else if (arg == "assigned") {
        Right(assigned)
      } else if (arg == "available") {
        Right(available)
      } else if (arg == "reserved") {
        Right(reserved)
      } else {
        Left(UnknownResourceStatusException)
      }
    }
  }

}
