package one.irradia.fieldrush.api

/**
 * A parser that ignores input.
 */

class FRParserIgnores : FRValueParserType<Unit> {
  override fun parse(context: FRParserContextType): FRParseResult<Unit> {
    return context.jsonStream.skip()
  }
}
