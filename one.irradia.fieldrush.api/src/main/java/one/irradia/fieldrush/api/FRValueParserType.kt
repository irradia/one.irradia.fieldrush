package one.irradia.fieldrush.api

/**
 * The type of value parsers.
 */

interface FRValueParserType<T> {

  /**
   * Execute the parser using the given context.
   */

  fun parse(context: FRParserContextType): FRParseResult<T>

  /**
   * Receive the result of parsing, assuming that the evaluations of all subparsers succeeded.
   */

  fun receive(context: FRParserContextType, result: T)

}
