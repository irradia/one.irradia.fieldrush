package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken
import com.google.common.base.Preconditions
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded

/**
 * An abstract implementation of an array parser.
 */

abstract class FRAbstractParserArray<T>(
  private val onReceive: (FRParserContextType, List<T>) -> Unit
) : FRParserArrayType<List<T>> {

  override fun parse(context: FRParserContextType): FRParseResult<List<T>> {
    context.trace(this.javaClass, "start: ${context.jsonStream.currentToken}")

    if (!context.jsonStream.isExpectedStartArrayToken) {
      val failure =
        context.failureOf<List<T>>("""Expected: '['
          | Received: ${context.jsonStream.currentToken}""".trimMargin())

      context.jsonStream.skip()
      return failure
    }

    val warnings = mutableListOf<FRParseWarning>()
    val errors = mutableListOf<FRParseError>()
    if (!this.moveToNextToken(context, errors)) {
      return FRParseFailed(
        warnings = warnings.toList(),
        errors = errors.toList()
      )
    }

    var index = 0
    while (true) {
      if (context.jsonStream.currentToken == null) {
        return context.failureOf("Unexpected end-of-stream")
      }

      if (context.jsonStream.currentToken == JsonToken.END_ARRAY) {
        break
      }

      val parser = this.forIndex(context, index)
      if (parser != null) {
        context.trace(this.javaClass, "${index}: created index parser ${parser.javaClass.simpleName}")
        val result = parser.parse(context.withNextDepth())
        when (result) {
          is FRParseSucceeded -> Unit
          is FRParseFailed -> errors.addAll(result.errors)
        }

        context.trace(this.javaClass, "${index}: completed index parser ${parser.javaClass.simpleName}")
      } else {
        context.trace(this.javaClass, "${index}: no index parser")
        this.skipContent(context, errors)
      }

      index += 1
    }

    Preconditions.checkArgument(
      context.jsonStream.currentToken == JsonToken.END_ARRAY,
      "Current token ${context.jsonStream.currentToken} must be ${JsonToken.END_ARRAY}")

    context.trace(this.javaClass, "end: ${context.jsonStream.currentToken}")

    this.skipContent(context, errors)
    return this.completeResult(
      warnings = warnings,
      errors = errors,
      context = context
    )
  }

  private fun skipContent(
    context: FRParserContextType,
    errors: MutableList<FRParseError>
  ): Boolean {
    val skip = context.jsonStream.skip()
    if (skip is FRParseFailed) {
      errors.addAll(skip.errors)
      return false
    }
    return true
  }

  private fun moveToNextToken(
    context: FRParserContextType,
    errors: MutableList<FRParseError>
  ): Boolean {
    val nextToken = context.jsonStream.nextToken()
    if (nextToken is FRParseFailed) {
      errors.addAll(nextToken.errors)
      return false
    }
    return true
  }

  private fun completeResult(
    warnings: MutableList<FRParseWarning>,
    errors: MutableList<FRParseError>,
    context: FRParserContextType
  ): FRParseResult<List<T>> =
    FRParseResult.errorsOr(
      warnings = warnings.toList(),
      errors = errors.toList()
    ) { this.onCompleted(context) }
      .onSuccess { this.onReceive.invoke(context, it) }
}
