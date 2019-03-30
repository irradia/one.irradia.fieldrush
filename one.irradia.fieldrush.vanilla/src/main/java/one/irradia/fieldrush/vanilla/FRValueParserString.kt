package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded
import one.irradia.fieldrush.api.FRParserContextType

/**
 * A scalar parser that expects string values.
 */

class FRValueParserString(
  onReceive: (FRParserContextType, String) -> Unit) : FRValueParserScalar<String>(onReceive) {

  override fun ofText(
    context: FRParserContextType,
    text: String): FRParseResult<String> {
    this.receive(context, text)
    return FRParseSucceeded(text)
  }

}
