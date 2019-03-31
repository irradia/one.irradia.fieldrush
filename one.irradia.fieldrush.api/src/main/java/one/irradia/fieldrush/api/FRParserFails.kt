package one.irradia.fieldrush.api

/**
 * A parser that fails for all input.
 */

class FRParserFails<T> : FRValueParserType<T> {
  override fun parse(context: FRParserContextType): FRParseResult<T> {
    context.jsonStream.skip()
    return context.failureOf("Parser failed")
  }
}

