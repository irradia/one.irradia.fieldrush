package one.irradia.fieldrush.vanilla

import com.fasterxml.jackson.core.JsonToken
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An abstract scalar (or null) parser.
 */

abstract class FRValueParserScalarOrNull<T>(
  private val onReceive: (FRParserContextType, T?) -> Unit
) : FRValueParserType<T?> {

  /**
   * Called when text must be converted into a more specific type.
   */

  abstract fun ofText(
    context: FRParserContextType,
    text: String
  ): FRParseResult<T>

  final override fun parse(context: FRParserContextType): FRParseResult<T?> {
    val token = context.jsonStream.currentToken

    return when {
      token == null || token == JsonToken.VALUE_NULL -> {
        context.jsonStream.skip()
        this.onReceive.invoke(context, null)
        FRParseResult.succeed(null)
      }

      token.isScalarValue -> {
        val text = context.jsonStream.currentText
        context.jsonStream.skip()
        this.ofText(context, text).map { x -> this.onReceive.invoke(context, x);x }
      }

      else -> {
        context.jsonStream.skip()
        context.failureOf("""Expected: A scalar value
          | Received: ${token}""".trimMargin())
      }
    }
  }
}
