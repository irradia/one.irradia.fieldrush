package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserScalarOrObject
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * A parser that delegates behaviour to functions.
 */

class FRParserScalarOrObject<T>(
  val onReceive: (FRParserContextType, T) -> Unit,
  private val forScalar: (FRParserContextType) -> FRValueParserType<T>,
  private val forObject: (FRParserContextType) -> FRValueParserType<T>
) : FRAbstractParserScalarOrObject<T>(onReceive) {

  override fun onScalar(context: FRParserContextType): FRValueParserType<T> =
    this.forScalar.invoke(context)

  override fun onObject(context: FRParserContextType): FRValueParserType<T> =
    this.forObject.invoke(context)

}
