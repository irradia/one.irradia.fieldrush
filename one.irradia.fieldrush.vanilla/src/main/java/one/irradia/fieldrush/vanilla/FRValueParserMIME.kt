package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.mime.api.MIMEParserType
import one.irradia.mime.api.MIMEType

/**
 * A scalar parser that expects MIME type values.
 */

class FRValueParserMIME(
  private val parsers: (String) -> MIMEParserType,
  onReceive: (FRParserContextType, MIMEType) -> Unit
) : FRValueParserScalar<MIMEType>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String
  ): FRParseResult<MIMEType> {
    return try {
      FRParseSucceeded(warnings = listOf(), result = this.parsers.invoke(text).parseOrException())
    } catch (e: Exception) {
      context.failureOf(
        message = """Problem: ${e.message}
            | Expected: A valid MIME type
            | Received: ${text}""".trimMargin(),
        exception = e)
    }
  }
}
