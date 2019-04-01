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
   * Monadic bind for parsers.
   */

  fun <U> flatMap(f: (T) -> FRParseResult<U>): FRValueParserType<U> =
    flatMap(this, f)

  /**
   * Monadic bind for parsers.
   */

  fun <U> map(f: (T) -> U): FRValueParserType<U> =
    map(this, f)

  companion object {

    /**
     * Monadic bind for parsers.
     */

    fun <A, B> flatMap(
      x: FRValueParserType<A>,
      f: (A) -> FRParseResult<B>): FRValueParserType<B> {
      return object: FRValueParserType<B> {
        override fun parse(context: FRParserContextType): FRParseResult<B> {
          return x.parse(context).flatMap(f)
        }
      }
    }

    /**
     * Functor map for parsers.
     */

    fun <A, B> map(
      x: FRValueParserType<A>,
      f: (A) -> B): FRValueParserType<B> {
      return object: FRValueParserType<B> {
        override fun parse(context: FRParserContextType): FRParseResult<B> {
          return x.parse(context).map(f)
        }
      }
    }
  }
}
