package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken.END_ARRAY
import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.core.JsonToken.FIELD_NAME
import com.fasterxml.jackson.core.JsonToken.NOT_AVAILABLE
import com.fasterxml.jackson.core.JsonToken.START_ARRAY
import com.fasterxml.jackson.core.JsonToken.START_OBJECT
import com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT
import com.fasterxml.jackson.core.JsonToken.VALUE_FALSE
import com.fasterxml.jackson.core.JsonToken.VALUE_NULL
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT
import com.fasterxml.jackson.core.JsonToken.VALUE_STRING
import com.fasterxml.jackson.core.JsonToken.VALUE_TRUE

/**
 * An abstract implementation of parser accepting a scalar, array, or object.
 */

abstract class FRAbstractParserScalarArrayOrObject<T>(
  private val onReceive: (FRParserContextType, List<T>) -> Unit) : FRParserScalarArrayOrObjectType<T> {

  override fun parse(context: FRParserContextType): FRParseResult<List<T>> {
    context.trace(this.javaClass, "start: ${context.jsonStream.currentToken}")

    val result =
      when (context.jsonStream.currentToken) {
        START_OBJECT ->
          this.onObject(context)
            .parse(context)
            .map { x -> listOf(x) }

        START_ARRAY ->
          this.onArray(context)
            .parse(context)

        VALUE_STRING,
        VALUE_NUMBER_INT,
        VALUE_NUMBER_FLOAT,
        VALUE_TRUE,
        VALUE_FALSE,
        VALUE_NULL ->
          this.onScalar(context)
            .parse(context)
            .map { x -> listOf(x) }

        null,
        NOT_AVAILABLE,
        END_OBJECT,
        END_ARRAY,
        FIELD_NAME,
        VALUE_EMBEDDED_OBJECT ->
          context.failureOf("""Expected: one of '{', '[', or a scalar value
          | Received: ${context.jsonStream.currentToken}
        """.trimMargin())
      }

    return result.onSuccess { x -> this.onReceive.invoke(context, x) }
  }
}