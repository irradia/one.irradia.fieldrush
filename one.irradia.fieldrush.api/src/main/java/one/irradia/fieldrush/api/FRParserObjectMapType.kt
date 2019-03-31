package one.irradia.fieldrush.api

/**
 * The type of parsers that consume JSON objects and produce maps as a result.
 */

interface FRParserObjectMapType<T> : FRValueParserType<Map<String, T>> {

  /**
   * Obtain a parser for the given key.
   */

  fun forKey(
    context: FRParserContextType,
    name: String): FRValueParserType<T>

}
