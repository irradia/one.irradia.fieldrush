package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat

/**
 * A scalar parser that expects date/time values.
 */

class FRValueParserDateTimeUTC(
  onReceive: (FRParserContextType, DateTime) -> Unit
) : FRValueParserScalar<DateTime>(onReceive) {

  private val dateParserWithTimezone =
    ISODateTimeFormat.dateTimeParser()
      .withOffsetParsed()

  private val dateParserWithUTC =
    ISODateTimeFormat.dateTimeParser()
      .withZoneUTC()

  /**
   * Correctly parse a date/time value.
   *
   * This slightly odd function first attempts to parse the incoming string as if it was
   * a date/time string with an included time zone. If the time string turned out not to
   * include a time zone, Joda Time will parse it using the system's default timezone. We
   * then detect that this has happened and, if the current system's timezone isn't UTC,
   * we parse the string *again* but this time assuming a UTC timezone.
   */

  private fun parseTime(
    timeText: String
  ): DateTime {
    val defaultZone = DateTimeZone.getDefault()
    val timeParsedWithZone = this.dateParserWithTimezone.parseDateTime(timeText)
    if (timeParsedWithZone.zone == defaultZone && defaultZone != DateTimeZone.UTC) {
      return this.dateParserWithUTC.parseDateTime(timeText)
    }
    return timeParsedWithZone.toDateTime(DateTimeZone.UTC)
  }

  override fun ofText(
    context: FRParserContextType,
    text: String
  ): FRParseResult<DateTime> {
    return try {
      FRParseSucceeded(warnings = listOf(), result = parseTime(text))
    } catch (e: IllegalArgumentException) {
      context.failureOf(
        message = """Problem: ${e.message}
          | Expected: A valid timestamp
          | Received: ${text}""".trimMargin(),
        exception = e)
    }
  }
}
