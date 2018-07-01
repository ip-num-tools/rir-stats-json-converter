package io.geekabyte.rirstats.exceptions

sealed trait ParseException
case class HeaderLineNotFoundException() extends ParseException
case class InvalidLine(lines:List[String]) extends ParseException
case class UnknownRegistryException() extends ParseException
case class UnknownResourceException() extends ParseException
case class UnknownResourceStatusException() extends ParseException
case class InvalidValue(exception:Throwable, message:String) extends ParseException
case class RIRStateJsonConversionException(message:String) extends ParseException