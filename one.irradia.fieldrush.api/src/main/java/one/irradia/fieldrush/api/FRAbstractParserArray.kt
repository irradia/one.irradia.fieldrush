package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken
import com.google.common.base.Preconditions
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded

/**
 * An abstract implementation of an array parser.
 */

abstract class FRAbstractParserArray<T>(
  private val onReceive: (FRParserContextType, List<T>) -> Unit) : FRParserArrayType<List<T>> {

  override fun parse(context: FRParserContextType): FRParseResult<List<T>> {
    context.trace(this.javaClass, "start: ${context.jsonParser.currentToken}")

    if (!context.jsonParser.isExpectedStartArrayToken) {
      val failure =
        context.failureOf<List<T>>("""Expected: '['
          | Received: ${context.jsonParser.currentToken}""".trimMargin())
      this.skip(context)
      return failure
    }

    context.jsonParser.nextToken()
    val errors = mutableListOf<FRParseError>()
    var index = 0
    while (true) {
      if (context.jsonParser.currentToken == JsonToken.END_ARRAY) {
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
        this.skip(context)
      }

      index += 1
    }

    Preconditions.checkArgument(
      context.jsonParser.currentToken == JsonToken.END_ARRAY,
      "Current token ${context.jsonParser.currentToken} must be ${JsonToken.END_ARRAY}")

    context.trace(this.javaClass, "end: ${context.jsonParser.currentToken}")
    context.jsonParser.nextToken()

    return if (errors.isEmpty()) {
      this.onIndicesCompleted(context)
        .flatMap { items ->
          this.onReceive.invoke(context, items)
          FRParseResult.succeed(items)
        }
    } else {
      FRParseFailed(errors.toList())
    }
  }

  private fun skip(context: FRParserContextType) {
    val before = context.jsonParser.currentToken
    context.trace(this.javaClass, "skip: before ${before}")

    if (before.isStructStart) {
      context.jsonParser.skipChildren()
    }

    context.jsonParser.nextToken()
    context.trace(this.javaClass, "skip: after ${context.jsonParser.currentToken}")
  }
}