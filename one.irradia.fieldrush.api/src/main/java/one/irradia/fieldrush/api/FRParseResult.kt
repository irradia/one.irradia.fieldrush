package one.irradia.fieldrush.api

/**
 * The result of parsing a document.
 */

sealed class FRParseResult<T> {

  /**
   * Parsing succeeded.
   */

  data class FRParseSucceeded<T>(

    /**
     * The parsed value.
     */

    val result: T) : FRParseResult<T>()

  /**
   * Parsing failed.
   */

  data class FRParseFailed<T>(

    /**
     * The list of parse errors.
     */

    val errors: List<FRParseError>) : FRParseResult<T>() {

    /**
     * Cast this failure value to a different type.
     */

    fun <U> cast(): FRParseFailed<U> =
      FRParseFailed(this.errors)
  }

  companion object {

    /**
     * Functor map.
     * If r == FRParseSucceeded(x), return FRParseSucceeded(f(x))
     * If r == FRParseFailed(y), return FRParseFailed(y)
     */

    fun <A, B> map(x: FRParseResult<A>, f: (A) -> B): FRParseResult<B> {
      return when (x) {
        is FRParseSucceeded -> FRParseSucceeded(f.invoke(x.result))
        is FRParseFailed -> FRParseFailed(x.errors)
      }
    }

    /**
     * Monadic bind.
     * If r == FRParseSucceeded(x), return f(r)
     * If r == FRParseFailed(y), return FRParseFailed(y)
     */

    fun <A, B> flatMap(x: FRParseResult<A>, f: (A) -> FRParseResult<B>): FRParseResult<B> {
      return when (x) {
        is FRParseSucceeded -> f.invoke(x.result)
        is FRParseFailed -> FRParseFailed(x.errors)
      }
    }

    /**
     * Construct a successful parse result.
     */

    fun <A> succeed(x: A): FRParseResult<A> {
      return FRParseSucceeded(x)
    }
  }

  /**
   * Functor map.
   * If r == FRParseSucceeded(x), return FRParseSucceeded(f(x))
   * If r == FRParseFailed(y), return FRParseFailed(y)
   */

  fun <U> map(f: (T) -> U): FRParseResult<U> =
    map(this, f)

  /**
   * Monadic bind.
   * If r == FRParseSucceeded(x), return f(r)
   * If r == FRParseFailed(y), return FRParseFailed(y)
   */

  fun <U> flatMap(f: (T) -> FRParseResult<U>): FRParseResult<U> =
    flatMap(this, f)
}
