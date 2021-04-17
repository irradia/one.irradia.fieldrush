package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType
import java.math.BigInteger

/**
 * A scalar parser that expects integer values.
 */

class FRValueParserInteger(
  onReceive: (FRParserContextType, BigInteger) -> Unit) : FRValueParserScalar<BigInteger>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<BigInteger> {
    return try {
      FRParseSucceeded(warnings = listOf(), result = BigInteger(text))
    } catch (e: NumberFormatException) {
      context.failureOf(
        """Problem: ${e.message}
            | Expected: An integer
            | Received: ${text}""".trimMargin())
    }
  }
}
