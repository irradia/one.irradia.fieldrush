package one.irradia.fieldrush.vanilla

import com.fasterxml.jackson.core.JsonParser
import one.irradia.fieldrush.api.FRLexicalPosition
import one.irradia.fieldrush.api.FRParseError
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParserContextType
import org.slf4j.Logger
import java.net.URI

internal class FRParserContext internal constructor(
  private val depth: Int,
  override val documentURI: URI,
  override val jsonParser: JsonParser,
  private val logger: Logger) : FRParserContextType {

  override fun withNextDepth(): FRParserContextType {
    return FRParserContext(
      depth = this.depth + 1,
      documentURI = this.documentURI,
      jsonParser = this.jsonParser,
      logger = this.logger)
  }

  private fun makeMessage(caller: Class<*>, message: String): String {
    return StringBuilder()
      .append(String.format("[%2d]", this.depth))
      .append(String.format("[%-16s]", caller.simpleName))
      .append(String.format(" %4d:%-4d: ",
        this.jsonParser.currentLocation.lineNr,
        this.jsonParser.currentLocation.columnNr))
      .append(message)
      .toString()
  }

  override fun trace(caller: Class<*>, message: String) {
    if (this.logger.isTraceEnabled) {
      this.logger.trace(this.makeMessage(caller, message))
    }
  }

  override fun debug(caller: Class<*>, message: String) {
    if (this.logger.isDebugEnabled) {
      this.logger.debug(this.makeMessage(caller, message))
    }
  }

  override fun errorOf(message: String, exception: Exception?): FRParseError {
    return FRParseError(
      producer = "core",
      position = FRLexicalPosition(
        source = this.documentURI,
        line = this.jsonParser.currentLocation.lineNr,
        column = this.jsonParser.currentLocation.columnNr),
      message = message,
      exception = exception)
  }

  override fun <T> failureOf(message: String, exception: Exception?): FRParseFailed<T> {
    return FRParseFailed(listOf(this.errorOf(message, exception)))
  }
}
