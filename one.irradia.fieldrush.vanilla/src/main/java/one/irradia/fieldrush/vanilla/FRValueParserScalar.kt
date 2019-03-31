package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An abstract scalar parser.
 */

abstract class FRValueParserScalar<T>(
  private val onReceive: (FRParserContextType, T) -> Unit) : FRValueParserType<T> {

  /**
   * Called when text must be converted into a more specific type.
   */

  abstract fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<T>

  final override fun parse(context: FRParserContextType): FRParseResult<T> {
    val token = context.jsonParser.currentToken
    return if (token.isScalarValue) {
      val text = context.jsonParser.text
      context.jsonParser.nextToken()
      this.ofText(context, text)
        .map { x -> this.onReceive.invoke(context, x);x }
    } else {
      if (token.isStructStart) {
        context.jsonParser.skipChildren()
      }
      context.jsonParser.nextToken()
      context.failureOf("""Expected: A scalar value
        | Received: ${token}""".trimMargin())
    }
  }
}
