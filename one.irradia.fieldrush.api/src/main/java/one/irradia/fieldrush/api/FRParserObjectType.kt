package one.irradia.fieldrush.api

/**
 * The type of parsers that consume JSON objects.
 */

interface FRParserObjectType<T> : FRValueParserType<T> {

  /**
   * Called if the executions of all parsers for all fields succeeded.
   */

  fun onFieldsCompleted(
    context: FRParserContextType)
    : FRParseResult<T>

  /**
   * Return a parser for the given named field, or `null` if the field should be ignored.
   */

  fun forField(
    context: FRParserContextType,
    name: String)
    : FRValueParserType<*>?

}
