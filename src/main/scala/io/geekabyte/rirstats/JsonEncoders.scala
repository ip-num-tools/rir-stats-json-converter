package io.geekabyte.rirstats

import java.text.SimpleDateFormat
import java.util.{Date => JDate}

import io.circe.{Encoder, Json}
import io.geekabyte.rirstats.models.{RecordCount, RirUtcOffset, SerialNumber}

object JsonEncoders {
  implicit val encodeJavaDate: Encoder[JDate] = (date: JDate) => {
    val formatter = new SimpleDateFormat("yyyyMMdd")
    Json.fromString(formatter.format(date))
  }

  implicit val encodeSerialNumber: Encoder[SerialNumber] = (serialNumber: SerialNumber) => {
    Json.fromInt(serialNumber.serialNumber)
  }

  implicit val encodeRecordCount: Encoder[RecordCount] = (recordCount: RecordCount) => {
    Json.fromInt(recordCount.recordCount)
  }

  implicit val encodeRirUtcOffset: Encoder[RirUtcOffset] = (rirUtcOffset: RirUtcOffset) => {
    Json.fromString(rirUtcOffset.rirUtcOffset)
  }

  import shapeless._
  import shapeless.labelled.{ FieldType, field }

  trait IsEnum[C <: Coproduct] {
    def to(c: C): String
    def from(s: String): Option[C]
  }

  object IsEnum {
    implicit val cnilIsEnum: IsEnum[CNil] = new IsEnum[CNil] {
      def to(c: CNil): String = sys.error("Impossible")
      def from(s: String): Option[CNil] = None
    }

    implicit def cconsIsEnum[K <: Symbol, H <: Product, T <: Coproduct](implicit
                                                                        witK: Witness.Aux[K],
                                                                        witH: Witness.Aux[H],
                                                                        gen: Generic.Aux[H, HNil],
                                                                        tie: IsEnum[T]
                                                                       ): IsEnum[FieldType[K, H] :+: T] = new IsEnum[FieldType[K, H] :+: T] {
      def to(c: FieldType[K, H] :+: T): String = c match {
        case Inl(h) => witK.value.name
        case Inr(t) => tie.to(t)
      }
      def from(s: String): Option[FieldType[K, H] :+: T] =
        if (s == witK.value.name) Some(Inl(field[K](witH.value)))
        else tie.from(s).map(Inr(_))
    }
  }

  import io.circe.Encoder

  implicit def encodeEnum[A, C <: Coproduct](implicit
                                             gen: LabelledGeneric.Aux[A, C],
                                             rie: IsEnum[C]
                                            ): Encoder[A] = Encoder[String].contramap[A](a => rie.to(gen.to(a)))

}
