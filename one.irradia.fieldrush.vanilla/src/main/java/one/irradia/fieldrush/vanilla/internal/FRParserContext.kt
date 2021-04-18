package one.irradia.fieldrush.vanilla.internal

import one.irradia.fieldrush.api.FRJSONStreamType
import one.irradia.fieldrush.api.FRParseError
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseWarning
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserProviderType
import org.slf4j.Logger
import java.net.URI

internal class FRParserContext internal constructor(
  private val depth: Int,
  override val documentURI: URI,
  override val jsonStream: FRJSONStreamType,
  override val parsers: FRValueParserProviderType,
  private val logger: Logger
) : FRParserContextType {

  private val warningLog = mutableListOf<FRParseWarning>()

  override fun withNextDepth(): FRParserContextType {
    return FRParserContext(
      depth = this.depth + 1,
      documentURI = this.documentURI,
      jsonStream = this.jsonStream,
      logger = this.logger,
      parsers = this.parsers
    )
  }

  private fun makeMessage(
    caller: Class<*>,
    message: String
  ): String {
    return StringBuilder()
      .append(String.format("[%2d]", this.depth))
      .append(String.format("[%-16s]", caller.simpleName))
      .append(String.format(" %4d:%-4d: ",
        this.jsonStream.currentPosition.line,
        this.jsonStream.currentPosition.column))
      .append(message)
      .toString()
  }

  override fun trace(
    caller: Class<*>,
    message: String
  ) {
    if (this.logger.isTraceEnabled) {
      this.logger.trace(this.makeMessage(caller, message))
    }
  }

  override fun debug(
    caller: Class<*>,
    message: String
  ) {
    if (this.logger.isDebugEnabled) {
      this.logger.debug(this.makeMessage(caller, message))
    }
  }

  override fun errorOf(
    message: String,
    exception: Exception?
  ): FRParseError {
    return FRParseError(
      producer = "core",
      position = this.jsonStream.currentPosition,
      message = message,
      exception = exception)
  }

  override fun <T> failureOf(
    message: String,
    exception: Exception?
  ): FRParseFailed<T> {
    return FRParseFailed(
      warnings = this.warnings.toList(),
      errors = listOf(this.errorOf(message, exception))
    )
  }

  override fun warn(
    caller: Class<*>,
    message: String
  ) {
    if (this.logger.isWarnEnabled) {
      this.logger.warn(this.makeMessage(caller, message))
    }

    this.warningLog.add(
      FRParseWarning(
        producer = caller.canonicalName,
        position = this.jsonStream.currentPosition,
        message = message,
        exception = null
      )
    )
  }

  override val warnings: List<FRParseWarning>
    get() = this.warningLog
}
