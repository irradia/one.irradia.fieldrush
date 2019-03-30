package one.irradia.fieldrush.api

import java.io.InputStream
import java.net.URI

interface FRParserProviderType {

  fun <T> createParser(
    uri: URI,
    stream: InputStream, rootParser: FRValueParserType<T>): FRParserType<T>

}
