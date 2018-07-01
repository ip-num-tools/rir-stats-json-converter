package io.geekabyte.rirstats

import cats.data.{NonEmptyList, Validated}
import io.geekabyte.rirstats.exceptions.{UnknownRegistryException, UnknownResourceException, UnknownResourceStatusException}
import io.geekabyte.rirstats.models.{Registry, ResourceStatus, ResourceType, afrinic, allocated, apnic, arin, asn, assigned, available, iana, ipv4, ipv6, lacnic, reserved, ripencc}
import cats.implicits._

object RegistryPatterns {
  def unapply(arg: String): Option[Registry] = {
    if (arg == "afrinic") {
      Some(afrinic)
    } else if(arg == "apnic") {
      Some(apnic)
    } else if (arg == "arin") {
      Some(arin)
    } else if (arg == "iana") {
      Some(iana)
    } else if (arg == "lacnic") {
      Some(lacnic)
    } else if (arg == "ripencc") {
      Some(ripencc)
    } else {
      None
    }
  }
}

object ResourceTypesPatterns {
  def unapply(arg: String): Option[ResourceType] = {
    if (arg == "asn") {
      Some(asn)
    } else if (arg == "ipv4") {
      Some(ipv4)
    } else if (arg == "ipv6") {
      Some(ipv6)
    } else {
      None
    }
  }
}

object ResourceStatusPattern {
  def unapply(arg: String): Option[ResourceStatus] = {
    if (arg == "allocated") {
      Some(allocated)
    } else if (arg == "assigned") {
      Some(assigned)
    } else if (arg == "available") {
      Some(available)
    } else if (arg == "reserved") {
      Some(reserved)
    } else {
      None
    }
  }
}

object ImplicitOps {
  implicit class StringRegistry(arg:String) {
    def toRegistry: Validated[NonEmptyList[UnknownRegistryException], Registry] = {
      arg match {
        case RegistryPatterns(registry) => registry.valid[NonEmptyList[UnknownRegistryException]]
        case _ => NonEmptyList.of(UnknownRegistryException()).invalid[Registry]
      }
    }
  }
  implicit class StringResourceType(arg: String) {
    def toResourceType: Validated[NonEmptyList[UnknownResourceException], ResourceType] = {
      arg match {
        case ResourceTypesPatterns(resourceType) => resourceType.valid[NonEmptyList[UnknownResourceException]]
        case _ => NonEmptyList.of(UnknownResourceException()).invalid[ResourceType]
      }
    }
  }
  implicit class StringResourceStatus(arg: String) {
    def toResourceStatus: Validated[NonEmptyList[UnknownResourceStatusException], ResourceStatus] = {
      arg match {
        case ResourceStatusPattern(resourceStatus) => resourceStatus.valid[NonEmptyList[UnknownResourceStatusException]]
        case _ => NonEmptyList.of(UnknownResourceStatusException()).invalid[ResourceStatus]
      }
    }
  }

}
