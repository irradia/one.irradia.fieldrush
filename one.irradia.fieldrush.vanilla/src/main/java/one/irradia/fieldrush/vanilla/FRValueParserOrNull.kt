package one.irradia.fieldrush.vanilla

import com.fasterxml.jackson.core.JsonToken
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * A parser that allows an existing parser to accept null values.
 */

class FRValueParserOrNull<T>(
  private val existing: FRValueParserType<T>
) : FRValueParserType<T?> {

  override fun parse(context: FRParserContextType): FRParseResult<T?> {
    context.trace(this.javaClass, "start: ${context.jsonStream.currentToken}")

    return when (context.jsonStream.currentToken) {
      null, JsonToken.VALUE_NULL -> {
        context.jsonStream.skip()
        FRParseResult.succeed(warnings = listOf(), x = null)
      }
      else -> {
        this.existing.parse(context).map { x -> x }
      }
    }
  }
}
