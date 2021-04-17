package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType
import java.net.URI
import java.net.URISyntaxException

/**
 * A scalar parser that expects URI values.
 */

class FRValueParserURI(
  onReceive: (FRParserContextType, URI) -> Unit) : FRValueParserScalar<URI>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<URI> {
    return try {
      FRParseSucceeded(warnings = listOf(), result = URI(text))
    } catch (e: URISyntaxException) {
      context.failureOf(
        message = """Problem: ${e.message}
          | Expected: A valid URI
          | Received: ${text}""".trimMargin(),
        exception = e)
    }
  }
}
