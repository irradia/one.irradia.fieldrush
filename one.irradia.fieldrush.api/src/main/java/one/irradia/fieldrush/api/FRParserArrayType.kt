package one.irradia.fieldrush.api

/**
 * The type of parsers that process homogeneous arrays.
 */

interface FRParserArrayType<T> : FRValueParserType<T> {

  /**
   * Called when the parsers for all indices have completed successfully.
   */

  fun onIndicesCompleted(
    context: FRParserContextType)
    : FRParseResult<T>

  /**
   * Obtain a parser for index `index`.
   */

  fun forIndex(
    context: FRParserContextType,
    index: Int)
    : FRValueParserType<*>?

}
