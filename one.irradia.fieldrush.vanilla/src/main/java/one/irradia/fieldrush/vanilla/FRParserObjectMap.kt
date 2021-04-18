package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserObjectMap
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * A parser that consumes an object and produces a map by constructing parsers from the given
 * function.
 */

class FRParserObjectMap<T>(
  private val forKey: (FRParserContextType, String) -> FRValueParserType<T>,
  onReceive: (FRParserContextType, Map<String, T>) -> Unit
) : FRAbstractParserObjectMap<T>(onReceive) {

  override fun forKey(context: FRParserContextType, name: String): FRValueParserType<T> =
    this.forKey.invoke(context, name)
}
