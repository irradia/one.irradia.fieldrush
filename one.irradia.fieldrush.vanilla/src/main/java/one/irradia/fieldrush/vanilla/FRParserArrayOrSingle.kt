package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserArrayOrSingle
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * A parser that delegates behaviour to functions.
 */

class FRParserArrayOrSingle<T>(
  val onReceive: (FRParserContextType, List<T>) -> Unit,
  private val forItem: (FRParserContextType) -> FRValueParserType<T>)
  : FRAbstractParserArrayOrSingle<T>(onReceive) {

  override fun onArray(context: FRParserContextType): FRValueParserType<List<T>> =
    FRParserArrayMonomorphic(this.onReceive, this.forItem)

  override fun onSingle(context: FRParserContextType): FRValueParserType<T> =
    this.forItem.invoke(context)
}
