package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonParser
import java.net.URI

interface FRParserContextType {

  val documentURI: URI

  val jsonParser: JsonParser

  fun withNextDepth(): FRParserContextType

  fun trace(caller: Class<*>, message: String)

  fun debug(caller: Class<*>, message: String)

  fun errorOf(
    message: String,
    exception: Exception? = null): FRParseError

  fun <T> failureOf(
    message: String,
    exception: Exception? = null): FRParseResult<T>
  
}