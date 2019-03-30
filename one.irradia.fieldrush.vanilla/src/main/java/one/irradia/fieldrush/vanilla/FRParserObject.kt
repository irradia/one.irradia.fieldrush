package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserObject
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRValueParserType

/**
 * An object parser that receives behaviour as functions.
 */

class FRParserObject<T>(
  receiver: (FRParserContextType, T) -> Unit,
  private val onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
  private val forField: (FRParserContextType, String) -> FRValueParserType<*>)
  : FRAbstractParserObject<T>(receiver) {

  override fun onFieldsCompleted(context: FRParserContextType): FRParseResult<T> {
    return this.onFieldsCompleted.invoke(context)
  }

  override fun forField(context: FRParserContextType, name: String): FRValueParserType<*>? {
    return this.forField.invoke(context, name)
  }
}
