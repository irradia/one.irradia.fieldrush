package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserObject
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserObjectSchema

/**
 * An object parser that receives behaviour as functions.
 */

class FRParserObject<T>(
  receiver: (FRParserContextType, T) -> Unit,
  private val onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
  private val onSchema: (FRParserContextType) -> FRParserObjectSchema
) : FRAbstractParserObject<T>(receiver) {

  override fun schema(context: FRParserContextType): FRParserObjectSchema =
    this.onSchema.invoke(context)

  override fun onCompleted(context: FRParserContextType): FRParseResult<T> =
    this.onFieldsCompleted.invoke(context)
}
