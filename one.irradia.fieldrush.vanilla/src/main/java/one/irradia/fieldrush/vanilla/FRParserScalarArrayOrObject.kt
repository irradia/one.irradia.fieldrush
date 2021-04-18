package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserScalarArrayOrObject
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * A parser that delegates behaviour to functions.
 */

class FRParserScalarArrayOrObject<T>(
  val onReceive: (FRParserContextType, List<T>) -> Unit,
  private val forScalar: (FRParserContextType) -> FRValueParserType<T>,
  private val forArray: (FRParserContextType) -> FRValueParserType<List<T>>,
  private val forObject: (FRParserContextType) -> FRValueParserType<T>
) : FRAbstractParserScalarArrayOrObject<T>(onReceive) {

  override fun onScalar(context: FRParserContextType): FRValueParserType<T> =
    this.forScalar.invoke(context)

  override fun onArray(context: FRParserContextType): FRValueParserType<List<T>> =
    this.forArray.invoke(context)

  override fun onObject(context: FRParserContextType): FRValueParserType<T> =
    this.forObject.invoke(context)

}
