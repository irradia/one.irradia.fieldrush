package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType

/**
 * A scalar parser that validates and accepts null.
 */

class FRValueParserValidOrNull<T>(
  onReceive: (FRParserContextType, T?) -> Unit,
  private val validate: (FRParserContextType, String) -> FRParseResult<T>)
  : FRValueParserScalarOrNull<T>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<T> {
    return this.validate.invoke(context, text)
  }
}
