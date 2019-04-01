package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType
import org.joda.time.Instant
import org.joda.time.format.ISODateTimeFormat


/**
 * A scalar parser that expects timestamp values.
 */

class FRValueParserTimestamp(
  onReceive: (FRParserContextType, Instant) -> Unit) : FRValueParserScalar<Instant>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<Instant> {
    return try {
      FRParseSucceeded(Instant.parse(text, ISODateTimeFormat.dateTimeParser()))
    } catch (e: IllegalArgumentException) {
      context.failureOf(
        message = """Problem: ${e.message}
          | Expected: A valid timestamp
          | Received: ${text}""".trimMargin(),
        exception = e)
    }
  }
}
