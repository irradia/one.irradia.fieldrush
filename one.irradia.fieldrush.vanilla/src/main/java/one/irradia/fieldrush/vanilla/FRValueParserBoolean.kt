package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType

/**
 * A scalar parser that expects boolean values.
 */

class FRValueParserBoolean(
  onReceive: (FRParserContextType, Boolean) -> Unit) : FRValueParserScalar<Boolean>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<Boolean> {
    val upper = text.toUpperCase()
    return when (upper) {
      "TRUE" ->
        FRParseSucceeded(true)
      "FALSE" ->
        FRParseSucceeded(false)
      else ->
        context.failureOf("""Expected: A boolean
            | Received: ${text}""".trimMargin())
    }
  }
}