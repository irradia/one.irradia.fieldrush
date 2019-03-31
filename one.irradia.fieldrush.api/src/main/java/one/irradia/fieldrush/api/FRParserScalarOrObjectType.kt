package one.irradia.fieldrush.api

/**
 * A parser that can execute one of a set of parsers based on the incoming JSON token.
 */

interface FRParserScalarOrObjectType<T> : FRValueParserType<T> {

  /**
   * Called if the object to be parsed turns out to be a scalar value.
   */

  fun onScalar(context: FRParserContextType): FRValueParserType<T>

  /**
   * Called if the object to be parsed turns out to be an object.
   */

  fun onObject(context: FRParserContextType): FRValueParserType<T>

}
