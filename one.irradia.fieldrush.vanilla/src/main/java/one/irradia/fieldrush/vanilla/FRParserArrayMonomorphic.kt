package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserArray
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An array parser for monomorphic arrays.
 */

class FRParserArrayMonomorphic<T>(
  onReceive: (FRParserContextType, List<T>) -> Unit,
  private val forEach: (FRParserContextType) -> FRValueParserType<T>)
  : FRAbstractParserArray<T>(onReceive) {

  private val values = mutableListOf<T>()

  override fun onIndicesCompleted(context: FRParserContextType): FRParseResult<List<T>> {
    return FRParseResult.succeed(this.values.toList())
  }

  override fun forIndex(context: FRParserContextType, index: Int): FRValueParserType<*>? {
    return object: FRValueParserType<T> {
      override fun parse(context: FRParserContextType): FRParseResult<T> {
        return forEach.invoke(context).parse(context).map { x -> values.add(x); x }
      }
    }
  }
}