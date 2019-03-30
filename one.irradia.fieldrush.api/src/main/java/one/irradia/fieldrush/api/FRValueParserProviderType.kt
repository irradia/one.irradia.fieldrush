package one.irradia.fieldrush.api

import one.irradia.mime.api.MIMEType
import java.math.BigInteger
import java.net.URI

/**
 * A provider of value parsers.
 */

interface FRValueParserProviderType {

  /**
   * Return a parser that consumes an integer.
   */

  fun forIntegerWithContext(receiver: (FRParserContextType, BigInteger) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<BigInteger>

  /**
   * Return a parser that consumes an integer.
   */

  fun forInteger(receiver: (BigInteger) -> Unit = ignoringReceiver())
    : FRValueParserType<BigInteger> {
    return this.forIntegerWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a floating-point value.
   */

  fun forRealWithContext(receiver: (FRParserContextType, Double) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<Double>

  /**
   * Return a parser that consumes a floating-point value.
   */

  fun forReal(receiver: (Double) -> Unit = ignoringReceiver())
    : FRValueParserType<Double> {
    return this.forRealWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a string value.
   */

  fun forStringWithContext(receiver: (FRParserContextType, String) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<String>

  /**
   * Return a parser that consumes a string value.
   */

  fun forString(receiver: (String) -> Unit = ignoringReceiver())
    : FRValueParserType<String> {
    return this.forStringWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a MIME type value.
   */

  fun forMIMEWithContext(receiver: (FRParserContextType, MIMEType) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<MIMEType>

  /**
   * Return a parser that consumes a MIME type value.
   */

  fun forMIME(receiver: (MIMEType) -> Unit = ignoringReceiver())
    : FRValueParserType<MIMEType> {
    return this.forMIMEWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a boolean value.
   */

  fun forBooleanWithContext(receiver: (FRParserContextType, Boolean) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<Boolean>

  /**
   * Return a parser that consumes a boolean value.
   */

  fun forBoolean(receiver: (Boolean) -> Unit = ignoringReceiver())
    : FRValueParserType<Boolean> {
    return this.forBooleanWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a URI value.
   */

  fun forURIWithContext(receiver: (FRParserContextType, URI) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<URI>

  /**
   * Return a parser that consumes a URI value.
   */

  fun forURI(receiver: (URI) -> Unit = ignoringReceiver())
    : FRValueParserType<URI> {
    return this.forURIWithContext { _, value -> receiver.invoke(value) }
  }

  /**
   * Return a parser that consumes a scalar value.
   */

  fun <T> forScalarWithContext(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<T>

  /**
   * Return a parser that consumes a scalar value.
   */

  fun <T> forScalar(
    validator: (String) -> FRParseResult<T>,
    receiver: (T) -> Unit = ignoringReceiver())
    : FRValueParserType<T> {
    return this.forScalarWithContext(
      validator = { _, value -> validator.invoke(value) },
      receiver = { _, value -> receiver.invoke(value) })
  }

  /**
   * Return a parser that consumes an object value.
   */

  fun <T> forObjectWithContext(
    forField: (FRParserContextType, String) -> FRValueParserType<*>,
    onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<T>

  /**
   * Return a parser that consumes an object value.
   */

  fun <T> forObject(
    forField: (String) -> FRValueParserType<*>,
    onFieldsCompleted: () -> FRParseResult<T>,
    receiver: (T) -> Unit = ignoringReceiver())
    : FRValueParserType<T> {
    return this.forObjectWithContext(
      forField = { _, field -> forField.invoke(field) },
      onFieldsCompleted = { onFieldsCompleted.invoke() },
      receiver = { _, value -> receiver.invoke(value) })
  }

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArrayWithContext(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<List<T>>

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArray(
    forIndex: (Int) -> FRValueParserType<*>,
    onIndicesCompleted: () -> FRParseResult<List<T>>,
    receiver: (List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>> {
    return this.forArrayWithContext(
      forIndex = { _, index -> forIndex.invoke(index) },
      onIndicesCompleted = { onIndicesCompleted.invoke() },
      receiver = { _, value -> receiver.invoke(value) })
  }

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArrayMonomorphicWithContext(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<List<T>>

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArrayMonomorphic(
    forEach: () -> FRValueParserType<T>,
    receiver: (List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>> {
    return this.forArrayMonomorphicWithContext(
      forEach = { forEach.invoke() },
      receiver = { _, value -> receiver.invoke(value) })
  }

  /**
   * A parser receiver that ignores the given results.
   */

  fun <T> ignoringReceiverWithContext(): (FRParserContextType, T) -> Unit =
    { _, _ -> }

  /**
   * A parser receiver that ignores the given results.
   */

  fun <T> ignoringReceiver(): (T) -> Unit =
    { _ -> }

}