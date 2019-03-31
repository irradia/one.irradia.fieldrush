package one.irradia.fieldrush.api

import java.io.InputStream
import java.net.URI

/**
 * A provider of parsers.
 */

interface FRParserProviderType {

  /**
   * Create a new parser.
   */

  fun <T> createParser(
    uri: URI,
    stream: InputStream,
    rootParser: FRValueParserType<T>): FRParserType<T>

}
