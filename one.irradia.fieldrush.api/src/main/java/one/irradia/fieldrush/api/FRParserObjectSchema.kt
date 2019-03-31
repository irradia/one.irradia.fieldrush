package one.irradia.fieldrush.api

/**
 * A schema for a single object.
 */

data class FRParserObjectSchema(

  /**
   * Schemas for the fields in the object.
   */

  val fields: List<FRParserObjectFieldSchema<*>>,

  /**
   * Create a parser for unknown fields.
   */

  val unknownField: (FRParserContextType, String) -> FRValueParserType<*> = { _, _ -> FRParserIgnores() }) {

  /**
   * The field schemas organized by name.
   */

  val fieldsByName: Map<String, FRParserObjectFieldSchema<*>> =
    this.fields.map { field -> Pair(field.name, field) }
      .toMap()

}
