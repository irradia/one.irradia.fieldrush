package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.core.JsonToken.FIELD_NAME
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import one.irradia.fieldrush.api.FRParseResult.FRParseSucceeded

/**
 * An abstract implementation of an object parser.
 */

abstract class FRAbstractParserObject<T>(
  private val onReceive: (FRParserContextType, T) -> Unit
) : FRParserObjectType<T> {

  override fun parse(context: FRParserContextType): FRParseResult<T> {
    context.trace(this.javaClass, "start: ${context.jsonStream.currentToken}")

    if (!context.jsonStream.isExpectedStartObjectToken) {
      val failure =
        context.failureOf<T>("""Expected: '{'
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

    val schema = this.schema(context)
    val requiredFieldNames =
      schema.fieldsByName
        .filterValues { fieldSchema -> !fieldSchema.isOptional }
        .map { fieldSchema -> fieldSchema.key }
        .toMutableSet()

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
      val fieldSchema = schema.fieldsByName[fieldName]

      if (!this.moveToNextToken(context, errors)) {
        break
      }

      val parser: FRValueParserType<out Any?>
      if (fieldSchema != null) {
        parser = fieldSchema.parser.invoke()
        context.trace(this.javaClass, "created field parser ${parser.javaClass.simpleName} for $fieldName")
      } else {
        context.trace(this.javaClass, "no field schema for field $fieldName")
        parser = schema.unknownField.invoke(context, fieldName)
      }

      val result = parser.parse(context.withNextDepth())
      when (result) {
        is FRParseSucceeded -> Unit
        is FRParseFailed -> errors.addAll(result.errors)
      }

      requiredFieldNames.remove(fieldName)
      context.trace(this.javaClass, "completed field parser ${parser.javaClass.simpleName} for $fieldName")
    }

    context.trace(this.javaClass, "end: ${context.jsonStream.currentToken}")
    this.moveToNextToken(context, errors)
    this.checkRequiredFields(requiredFieldNames, errors, context)
    return parseFinish(
      warnings = warnings,
      errors = errors,
      context = context
    )
  }

  private fun checkRequiredFields(
    requiredFieldNames: MutableSet<String>,
    errors: MutableList<FRParseError>,
    context: FRParserContextType
  ) {
    for (name in requiredFieldNames) {
      errors.add(context.errorOf("Missing a required field '${name}'"))
    }
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
    context: FRParserContextType
  ): FRParseResult<T> =
    FRParseResult.errorsOr(
      warnings = warnings.toList(),
      errors = errors.toList()
    ) { this.onCompleted(context) }
      .onSuccess { this.onReceive.invoke(context, it) }
}
