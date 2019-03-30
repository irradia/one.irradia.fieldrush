package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType

/**
 * A scalar parser that expects real number values.
 */

class FRValueParserReal(
  onReceive: (FRParserContextType, Double) -> Unit) : FRValueParserScalar<Double>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<Double> {
    return try {
      val value = java.lang.Double.parseDouble(text)
      this.receive(context, value)
      FRParseSucceeded(value)
    } catch (e: NumberFormatException) {
      context.failureOf(
        message = """Problem: ${e.message}
          | Expected: A floating-point value
          | Received: ${text}""".trimMargin(),
        exception = e)
    }
  }
}
