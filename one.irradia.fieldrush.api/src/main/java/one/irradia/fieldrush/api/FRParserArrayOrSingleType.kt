package one.irradia.fieldrush.api

/**
 * A parser that can execute one of a set of parsers based on the incoming JSON token.
 */

interface FRParserArrayOrSingleType<T> : FRValueParserType<List<T>> {

  /**
   * Called if the object to be parsed turns out to be a single item.
   */

  fun onSingle(context: FRParserContextType): FRValueParserType<T>

  /**
   * Called if the object to be parsed turns out to be an array.
   */

  fun onArray(context: FRParserContextType): FRValueParserType<List<T>>

}
