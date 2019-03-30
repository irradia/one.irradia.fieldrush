package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserArray
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An array parser that receives behaviour as functions.
 */

class FRParserArray<T>(
  receiver: (FRParserContextType, List<T>) -> Unit,
  private val onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
  private val forIndex: (FRParserContextType, Int) -> FRValueParserType<*>)
  : FRAbstractParserArray<T>(receiver) {
  override fun onIndicesCompleted(context: FRParserContextType): FRParseResult<List<T>> {
    return this.onIndicesCompleted.invoke(context)
  }

  override fun forIndex(context: FRParserContextType, index: Int): FRValueParserType<*>? {
    return this.forIndex.invoke(context, index)
  }
}