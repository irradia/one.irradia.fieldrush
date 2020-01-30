package one.irradia.fieldrush.vanilla

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import one.irradia.fieldrush.api.FRLexicalPosition
import one.irradia.fieldrush.api.FRParseError
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRParserType
import one.irradia.fieldrush.api.FRValueParserType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.net.URI

/**
 * A provider of parsers.
 *
 * Note: This class must have a no-argument public constructor in order to work correctly with [java.util.ServiceLoader]
 */

class FRParsers : FRParserProviderType {

  private val logger = LoggerFactory.getLogger(FRParsers::class.java)
  private val parsers = JsonFactory()

  override fun <T> createParser(
    uri: URI,
    stream: InputStream,
    rootParser: FRValueParserType<T>): FRParserType<T> {
    return Parser(
      documentURI = uri,
      jsonParser = this.parsers.createParser(stream),
      logger = this.logger,
      parsers = this,
      rootParser = rootParser,
      stream = stream
    )
  }

  private class Parser<T>(
    private val documentURI: URI,
    private val stream: InputStream,
    private val rootParser: FRValueParserType<T>,
    private val jsonParser: JsonParser,
    private val logger: Logger,
    private val parsers: FRParserProviderType
  ) : FRParserType<T> {

    private var closed = false

    override fun close() {
      if (!this.closed) {
        try {
          this.stream.close()
        } finally {
          this.closed = true
        }
      }
    }

    override fun parse(): FRParseResult<T> {
      if (this.closed) {
        throw IllegalStateException("Parser is closed")
      }

      val jsonStream: FRJSONStream
      try {
        jsonStream = FRJSONStream(this.documentURI, this.jsonParser)
      } catch (e: Exception) {
        return FRParseResult.FRParseFailed(
          errors = listOf(FRParseError(
            producer = "core",
            position = FRLexicalPosition(this.documentURI, 1, 0),
            message = e.message ?: "JSON stream failed",
            exception = e)))
      }

      val context =
        FRParserContext(
          depth = 0,
          documentURI = this.documentURI,
          jsonStream = jsonStream,
          logger = this.logger,
          parsers = this.parsers
        )

      return this.rootParser.parse(context).flatMap { data ->
        if (jsonStream.currentToken != null) {
          context.failureOf("Failed to consume all JSON input")
        } else {
          FRParseResult.succeed(data)
        }
      }
    }
  }
}
