package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An abstract scalar parser.
 */

abstract class FRValueParserScalar<T>(
  private val onReceive: (FRParserContextType, T) -> Unit) : FRValueParserType<T> {

  override fun receive(context: FRParserContextType, result: T) {
    this.onReceive.invoke(context, result)
  }

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
