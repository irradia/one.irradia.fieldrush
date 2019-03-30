package one.irradia.fieldrush.api

/**
 * A consumer of values for array parsers.
 */

interface FRValueParserArrayConsumerType<T> {

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
