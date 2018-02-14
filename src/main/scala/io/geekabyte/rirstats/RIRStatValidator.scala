package io.geekabyte.rirstats

import java.util

import com.networknt.schema.{JsonSchema, ValidationMessage}
import io.geekabyte.rirstats.exceptions.{ParseException, RIRStateJsonConversionException}
import io.geekabyte.rirstatschema.Schema


trait RIRStatValidator {
  def validate(content:String):Either[List[ParseException], String]
}

object validator extends RIRStatValidator {

  private lazy val schema: JsonSchema = {
    import com.networknt.schema.JsonSchemaFactory
    val schemaContent: String = Schema.schema.mkString
    JsonSchemaFactory.getInstance().getSchema(schemaContent)
  }
  override def validate(content:String): Either[List[ParseException], String] = {
    import com.fasterxml.jackson.databind.ObjectMapper
    val mapper = new ObjectMapper
    val node = mapper.readTree(content)
    val errors: util.Set[ValidationMessage] = schema.validate(node)
    if (errors.isEmpty) Right(content) else Left(List(RIRStateJsonConversionException(errors.toString)))
  }
}

object extendedValidator extends RIRStatValidator  {
  private lazy val extendedSchema: JsonSchema = {
    import com.networknt.schema.JsonSchemaFactory
    val schemaContent: String = Schema.extendedSchema.mkString
    JsonSchemaFactory.getInstance().getSchema(schemaContent)
  }
  override def validate(content:String): Either[List[ParseException], String] ={
    import com.fasterxml.jackson.databind.ObjectMapper
    val mapper = new ObjectMapper
    val node = mapper.readTree(content)
    val errors: util.Set[ValidationMessage] = extendedSchema.validate(node)
    if (errors.isEmpty) Right(content) else Left(List(RIRStateJsonConversionException(errors.toString)))
  }
}
