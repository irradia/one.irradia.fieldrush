package one.irradia.fieldrush.api

/**
 * The type of value parsers.
 */

interface FRValueParserType<T> {

  /**
   * Execute the parser using the given context.
   */

  fun parse(context: FRParserContextType): FRParseResult<T>

}
