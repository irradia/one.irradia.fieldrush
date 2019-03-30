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

  fun forInteger(
    receiver: (FRParserContextType, BigInteger) -> Unit = ignoringReceiver())
    : FRValueParserType<BigInteger>

  /**
   * Return a parser that consumes a floating-point value.
   */

  fun forReal(
    receiver: (FRParserContextType, Double) -> Unit = ignoringReceiver())
    : FRValueParserType<Double>

  /**
   * Return a parser that consumes a string value.
   */

  fun forString(
    receiver: (FRParserContextType, String) -> Unit = ignoringReceiver())
    : FRValueParserType<String>

  /**
   * Return a parser that consumes a MIME type value.
   */

  fun forMIME(
    receiver: (FRParserContextType, MIMEType) -> Unit = ignoringReceiver())
    : FRValueParserType<MIMEType>

  /**
   * Return a parser that consumes a boolean value.
   */

  fun forBoolean(
    receiver: (FRParserContextType, Boolean) -> Unit = ignoringReceiver())
    : FRValueParserType<Boolean>

  /**
   * Return a parser that consumes a URI value.
   */

  fun forURI(
    receiver: (FRParserContextType, URI) -> Unit = ignoringReceiver())
    : FRValueParserType<URI>

  /**
   * Return a parser that consumes a scalar value.
   */

  fun <T> forScalar(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiver())
    : FRValueParserType<T>

  /**
   * Return a parser that consumes an object value.
   */

  fun <T> forObject(
    forField: (FRParserContextType, String) -> FRValueParserType<*>,
    onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiver())
    : FRValueParserType<T>

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArray(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>>

  /**
   * Return a parser that consumes an array value.
   */

  fun <T> forArrayMonomorphic(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>>

  /**
   * A parser receiver that ignores the given results.
   */

  fun <T> ignoringReceiver(): (FRParserContextType, T) -> Unit =
    { _, _ -> }
}