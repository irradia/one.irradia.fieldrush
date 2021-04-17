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
     * The list of parse warnings.
     */

    val warnings: List<FRParseWarning>,

    /**
     * The parsed value.
     */

    val result: T
  ) : FRParseResult<T>()

  /**
   * Parsing failed.
   */

  data class FRParseFailed<T>(

    /**
     * The list of parse warnings.
     */

    val warnings: List<FRParseWarning>,

    /**
     * The list of parse errors.
     */

    val errors: List<FRParseError>
  ) : FRParseResult<T>()

  companion object {

    /**
     * Functor map.
     *
     * If r == FRParseSucceeded(x), return FRParseSucceeded(f(x))
     * If r == FRParseFailed(y), return FRParseFailed(y)
     */

    fun <A, B> map(
      x: FRParseResult<A>,
      f: (A) -> B
    ): FRParseResult<B> {
      return when (x) {
        is FRParseSucceeded ->
          FRParseSucceeded(
            result = f.invoke(x.result),
            warnings = x.warnings
          )
        is FRParseFailed ->
          FRParseFailed(
            warnings = x.warnings,
            errors = x.errors
          )
      }
    }

    /**
     * Monadic bind.
     *
     * If r == FRParseSucceeded(x), return f(r)
     * If r == FRParseFailed(y), return FRParseFailed(y)
     */

    fun <A, B> flatMap(
      x: FRParseResult<A>,
      f: (A) -> FRParseResult<B>
    ): FRParseResult<B> {
      return when (x) {
        is FRParseSucceeded ->
          when (val result = f.invoke(x.result)) {
            is FRParseFailed ->
              FRParseFailed(
                warnings = result.warnings.plus(x.warnings),
                errors = result.errors
              )
            is FRParseSucceeded ->
              FRParseSucceeded(
                warnings = result.warnings.plus(x.warnings),
                result = result.result
              )
          }
        is FRParseFailed ->
          FRParseFailed(
            warnings = x.warnings,
            errors = x.errors
          )
      }
    }

    /**
     * Construct a successful parse result.
     */

    fun <A> succeed(
      warnings: List<FRParseWarning>,
      x: A
    ): FRParseResult<A> {
      return FRParseSucceeded(warnings, x)
    }

    /**
     * Construct a successful parse result.
     */

    fun <A> succeed(
      x: A
    ): FRParseResult<A> {
      return FRParseSucceeded(listOf(), x)
    }

    /**
     * If `errors` is empty, then evaluate `orElse`. Otherwise, return a failure result with
     * `errors`.
     */

    fun <A> errorsOr(
      warnings: List<FRParseWarning> = listOf(),
      errors: List<FRParseError>,
      orElse: () -> FRParseResult<A>
    ): FRParseResult<A> =
      if (errors.isEmpty()) {
        orElse.invoke()
      } else {
        FRParseFailed(warnings, errors)
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

  /**
   * If this value is successful, evaluate `f` and return `this`.
   */

  fun onSuccess(f: (T) -> Unit): FRParseResult<T> {
    return when (this) {
      is FRParseSucceeded -> {
        f.invoke(this.result); this
      }
      is FRParseFailed -> this
    }
  }
}
