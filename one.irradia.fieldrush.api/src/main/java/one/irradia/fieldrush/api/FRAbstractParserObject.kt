package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken
import com.google.common.base.Preconditions
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded

/**
 * An abstract implementation of an object parser.
 */

abstract class FRAbstractParserObject<T>(
  private val onReceive: (FRParserContextType, T) -> Unit) : FRParserObjectType<T> {

  override fun parse(context: FRParserContextType): FRParseResult<T> {
    context.trace(this.javaClass, "start: ${context.jsonParser.currentToken}")

    if (!context.jsonParser.isExpectedStartObjectToken) {
      val failure =
        context.failureOf<T>("""Expected: '{'
          | Received: ${context.jsonParser.currentToken}""".trimMargin())
      this.skip(context)
      return failure
    }

    val errors = mutableListOf<FRParseError>()
    context.jsonParser.nextToken()

    while (true) {
      if (context.jsonParser.currentToken == JsonToken.END_OBJECT) {
        break
      }

      Preconditions.checkArgument(
        context.jsonParser.currentToken == JsonToken.FIELD_NAME,
        "Current token ${context.jsonParser.currentToken} must be ${JsonToken.FIELD_NAME}")

      val fieldName = context.jsonParser.currentName
      val parser = this.forField(context, fieldName)
      context.jsonParser.nextToken()

      if (parser != null) {
        context.trace(this.javaClass, "created field parser ${parser.javaClass.simpleName} for $fieldName")
        val result = parser.parse(context.withNextDepth())
        when (result) {
          is FRParseSucceeded -> Unit
          is FRParseFailed -> errors.addAll(result.errors)
        }

        context.trace(this.javaClass, "completed field parser ${parser.javaClass.simpleName} for $fieldName")
      } else {
        context.trace(this.javaClass, "no field parser for field $fieldName")
        this.skip(context)
      }
    }

    Preconditions.checkArgument(
      context.jsonParser.currentToken == JsonToken.END_OBJECT,
      "Current token ${context.jsonParser.currentToken} must be ${JsonToken.END_OBJECT}")

    context.trace(this.javaClass, "end: ${context.jsonParser.currentToken}")
    context.jsonParser.nextToken()

    return if (errors.isEmpty()) {
      this.onFieldsCompleted(context)
        .flatMap { result ->
          this.onReceive.invoke(context, result)
          FRParseSucceeded(result)
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