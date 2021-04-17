package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.core.JsonToken.FIELD_NAME
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded

/**
 * An abstract implementation of an object map parser.
 */

abstract class FRAbstractParserObjectMap<T>(
  private val onReceive: (FRParserContextType, Map<String, T>) -> Unit) : FRParserObjectMapType<T> {

  override fun parse(context: FRParserContextType): FRParseResult<Map<String, T>> {
    context.trace(this.javaClass, "start: ${context.jsonStream.currentToken}")

    if (!context.jsonStream.isExpectedStartObjectToken) {
      val failure =
        context.failureOf<Map<String, T>>("""Expected: '{'
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

    val map = mutableMapOf<String, T>()
    while (true) {
      context.trace(this.javaClass, "token: ${context.jsonStream.currentToken}")

      if (context.jsonStream.currentToken == END_OBJECT) {
        break
      }

      if (context.jsonStream.currentToken != FIELD_NAME) {
        errors.add(expectedFieldName(context))
        break
      }

      val fieldName = context.jsonStream.currentName
      val parser = this.forKey(context, fieldName)

      if (!this.moveToNextToken(context, errors)) {
        break
      }

      val result = parser.parse(context.withNextDepth())
      when (result) {
        is FRParseSucceeded -> map[fieldName] = result.result
        is FRParseFailed -> errors.addAll(result.errors)
      }

      context.trace(this.javaClass, "completed field parser ${parser.javaClass.simpleName} for $fieldName")
    }

    context.trace(this.javaClass, "end: ${context.jsonStream.currentToken}")
    this.moveToNextToken(context, errors)
    return parseFinish(
      warnings = warnings,
      errors = errors,
      context = context,
      map = map.toMap()
    )
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

  private fun expectedFieldName(context: FRParserContextType): FRParseError {
    return context.errorOf("""Expected: A field name
            | Received: ${context.jsonStream.currentToken}
          """.trimMargin())
  }

  private fun parseFinish(
    warnings: MutableList<FRParseWarning>,
    errors: MutableList<FRParseError>,
    context: FRParserContextType,
    map: Map<String, T>
  ): FRParseResult<Map<String, T>> =
    FRParseResult.errorsOr(
      warnings = warnings.toList(),
      errors = errors.toList()
    ) { FRParseResult.succeed(warnings, map) }
      .onSuccess { this.onReceive.invoke(context, it) }
}
