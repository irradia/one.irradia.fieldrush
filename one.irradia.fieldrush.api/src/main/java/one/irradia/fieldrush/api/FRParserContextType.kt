package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonParser
import java.net.URI

/**
 * The context of a parse operation in progress.
 */

interface FRParserContextType {

  /**
   * The URI of the document being parsed, for diagnostic purposes.
   */

  val documentURI: URI

  /**
   * An open JSON stream parser.
   */

  val jsonParser: JsonParser

  /**
   * Return a new context value with a depth one greater than that of the current context.
   */

  fun withNextDepth(): FRParserContextType

  /**
   * Log a message at `trace` severity.
   */

  fun trace(caller: Class<*>, message: String)

  /**
   * Log a message at `debug` severity.
   */

  fun debug(caller: Class<*>, message: String)

  /**
   * Construct a parse error using the given message and exception, if any.
   */

  fun errorOf(
    message: String,
    exception: Exception? = null): FRParseError

  /**
   * Construct a failure result using the given message and exception, if any.
   */

  fun <T> failureOf(
    message: String,
    exception: Exception? = null): FRParseResult<T>

}