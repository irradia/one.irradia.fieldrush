package one.irradia.fieldrush.api

import java.io.Closeable

interface FRParserType<T> : Closeable {

  fun parse(): FRParseResult<T>

}