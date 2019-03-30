package one.irradia.fieldrush.api

/**
 * A schema for a single object field.
 */

class FRParserObjectFieldSchema<T>(

  /**
   * The name of the field.
   */

  val name: String,

  /**
   * A function that will be used to construct a parser for the given field.
   */

  val parser: () -> FRValueParserType<T>,

  /**
   * `true` if the field is allowed to be missing from the parsed object.
   */

  val isOptional: Boolean = false)
