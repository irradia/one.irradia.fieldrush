package one.irradia.fieldrush.api

import java.io.Closeable

/**
 * A parser.
 */

interface FRParserType<T> : Closeable {

  /**
   * Execute the parser, returning the results of parsing.
   */

  fun parse(): FRParseResult<T>
}