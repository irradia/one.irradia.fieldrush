package one.irradia.fieldrush.api

/**
 * The type of parsers that consume JSON objects.
 */

interface FRParserObjectType<T> : FRValueParserType<T> {

  /**
   * Called if the executions of all parsers for all fields succeeded.
   */

  fun onCompleted(
    context: FRParserContextType)
    : FRParseResult<T>

  /**
   * Return the schema for the object.
   */

  fun schema(
    context: FRParserContextType)
    : FRParserObjectSchema

}
