package one.irradia.fieldrush.api

import one.irradia.mime.api.MIMEType
import org.joda.time.Instant
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
    : FRValueParserType<BigInteger> =
    this.forIntegerWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a floating-point value.
   */

  fun forRealWithContext(receiver: (FRParserContextType, Double) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<Double>

  /**
   * Return a parser that consumes a floating-point value.
   */

  fun forReal(receiver: (Double) -> Unit = ignoringReceiver())
    : FRValueParserType<Double> =
    this.forRealWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a string value.
   */

  fun forStringWithContext(receiver: (FRParserContextType, String) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<String>

  /**
   * Return a parser that consumes a string value.
   */

  fun forString(receiver: (String) -> Unit = ignoringReceiver())
    : FRValueParserType<String> =
    this.forStringWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a MIME type value.
   */

  fun forMIMEWithContext(receiver: (FRParserContextType, MIMEType) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<MIMEType>

  /**
   * Return a parser that consumes a MIME type value.
   */

  fun forMIME(receiver: (MIMEType) -> Unit = ignoringReceiver())
    : FRValueParserType<MIMEType> =
    this.forMIMEWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a boolean value.
   */

  fun forBooleanWithContext(receiver: (FRParserContextType, Boolean) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<Boolean>

  /**
   * Return a parser that consumes a boolean value.
   */

  fun forBoolean(receiver: (Boolean) -> Unit = ignoringReceiver())
    : FRValueParserType<Boolean> =
    this.forBooleanWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a URI value.
   */

  fun forURIWithContext(receiver: (FRParserContextType, URI) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<URI>

  /**
   * Return a parser that consumes a URI value.
   */

  fun forURI(receiver: (URI) -> Unit = ignoringReceiver())
    : FRValueParserType<URI> =
    this.forURIWithContext { _, value -> receiver.invoke(value) }

  /**
   * Return a parser that consumes a timestamp value.
   */

  fun forTimestampWithContext(receiver: (FRParserContextType, Instant) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<Instant>

  /**
   * Return a parser that consumes a timestamp value.
   */

  fun forTimestamp(receiver: (Instant) -> Unit = ignoringReceiver())
    : FRValueParserType<Instant> =
    this.forTimestampWithContext { _, value -> receiver.invoke(value) }

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
    : FRValueParserType<T> =
    this.forScalarWithContext(
      validator = { _, value -> validator.invoke(value) },
      receiver = { _, value -> receiver.invoke(value) })

  /**
   * Return a parser that consumes a scalar or null value.
   */

  fun <T> forScalarOrNullWithContext(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T?) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<T?>

  /**
   * Return a parser that consumes a scalar or null value.
   */

  fun <T> forScalarOrNull(
    validator: (String) -> FRParseResult<T>,
    receiver: (T?) -> Unit = ignoringReceiver())
    : FRValueParserType<T?> =
    this.forScalarOrNullWithContext(
      validator = { _, value -> validator.invoke(value) },
      receiver = { _, value -> receiver.invoke(value) })

  /**
   * Return a parser that consumes an object value.
   */

  fun <T> forObjectWithContext(
    onSchema: (FRParserContextType) -> FRParserObjectSchema,
    onCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<T>

  /**
   * Return a parser that consumes an object value.
   */

  fun <T> forObject(
    onSchema: (FRParserContextType) -> FRParserObjectSchema,
    onCompleted: () -> FRParseResult<T>,
    receiver: (T) -> Unit = ignoringReceiver())
    : FRValueParserType<T> =
    this.forObjectWithContext(
      onSchema = { context -> onSchema.invoke(context) },
      onCompleted = { onCompleted.invoke() },
      receiver = { _, value -> receiver.invoke(value) })

  /**
   * Return a parser that consumes an array.
   */

  fun <T> forArrayWithContext(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<List<T>>

  /**
   * Return a parser that consumes an array.
   */

  fun <T> forArray(
    forIndex: (Int) -> FRValueParserType<*>,
    onIndicesCompleted: () -> FRParseResult<List<T>>,
    receiver: (List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>> =
    this.forArrayWithContext(
      forIndex = { _, index -> forIndex.invoke(index) },
      onIndicesCompleted = { onIndicesCompleted.invoke() },
      receiver = { _, value -> receiver.invoke(value) })

  /**
   * Return a parser that consumes an array of objects of the same type.
   */

  fun <T> forArrayMonomorphicWithContext(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRValueParserType<List<T>>

  /**
   * Return a parser that consumes an array of objects of the same type.
   */

  fun <T> forArrayMonomorphic(
    forEach: () -> FRValueParserType<T>,
    receiver: (List<T>) -> Unit = ignoringReceiver())
    : FRValueParserType<List<T>> =
    this.forArrayMonomorphicWithContext(
      forEach = { forEach.invoke() },
      receiver = { _, value -> receiver.invoke(value) })

  /**
   * Return a parser that can parse a scalar, array, or object.
   */

  fun <T> forScalarArrayOrObjectWithContext(
    forScalar: (FRParserContextType) -> FRValueParserType<T>,
    forArray: (FRParserContextType) -> FRValueParserType<List<T>>,
    forObject: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRParserScalarArrayOrObjectType<T>

  /**
   * Return a parser that can parse a scalar, array, or object.
   */

  fun <T> forScalarArrayOrObject(
    forScalar: () -> FRValueParserType<T>,
    forArray: () -> FRValueParserType<List<T>>,
    forObject: () -> FRValueParserType<T>,
    receiver: (List<T>) -> Unit = ignoringReceiver())
    : FRParserScalarArrayOrObjectType<T> =
    this.forScalarArrayOrObjectWithContext(
      forScalar = { forScalar() },
      forArray = { forArray() },
      forObject = { forObject() })

  /**
   * Return a parser that can parse a scalar, or object.
   */

  fun <T> forScalarOrObjectWithContext(
    forScalar: (FRParserContextType) -> FRValueParserType<T>,
    forObject: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, T) -> Unit = ignoringReceiverWithContext())
    : FRParserScalarOrObjectType<T>

  /**
   * Return a parser that can parse a scalar, or object.
   */

  fun <T> forScalarOrObject(
    forScalar: () -> FRValueParserType<T>,
    forObject: () -> FRValueParserType<T>,
    receiver: (T) -> Unit = ignoringReceiver())
    : FRParserScalarOrObjectType<T> =
    this.forScalarOrObjectWithContext(
      forScalar = { forScalar() },
      forObject = { forObject() })

  /**
   * Return a parser that can parse an array, or an object.
   */

  fun <T> forArrayOrSingleWithContext(
    forItem: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit = ignoringReceiverWithContext())
    : FRParserArrayOrSingleType<T>

  /**
   * Return a parser that can parse a scalar, or object.
   */

  fun <T> forArrayOrSingle(
    forItem: () -> FRValueParserType<T>,
    receiver: (T) -> Unit = ignoringReceiver())
    : FRParserArrayOrSingleType<T> =
    this.forArrayOrSingleWithContext(forItem = { forItem() })

  /**
   * Return a parser that consumes an object and produces a map.
   */

  fun <T> forObjectMapWithContext(
    forKey: (FRParserContextType, String) -> FRValueParserType<T>,
    receiver: (FRParserContextType, Map<String, T>) -> Unit = ignoringReceiverWithContext())
    : FRParserObjectMapType<T>

  /**
   * Return a parser that consumes an object and produces a map.
   */

  fun <T> forObjectMap(
    forKey: (String) -> FRValueParserType<T>,
    receiver: (Map<String, T>) -> Unit = ignoringReceiver())
    : FRParserObjectMapType<T> {
    return this.forObjectMapWithContext(
      forKey = { _, key -> forKey.invoke(key) },
      receiver = { _, m -> receiver.invoke(m) })
  }

  /**
   * Return a parser derived from the given function.
   */

  fun <T> forFunction(f: (FRParserContextType) -> FRParseResult<T>): FRValueParserType<T> =
    object : FRValueParserType<T> {
      override fun parse(context: FRParserContextType): FRParseResult<T> = f.invoke(context)
    }

  /**
   * Given a parser that does not accept `null` values, return a version of the given
   * parser that does.
   */

  fun <T> acceptingNull(
    parser: FRValueParserType<T>
  ): FRValueParserType<T?>

  /**
   * Return a parser that always fails.
   */

  fun <T> fails(): FRValueParserType<T> =
    FRParserFails()

  /**
   * Return a parser that ignores all input.
   */

  fun ignores(): FRValueParserType<Unit> =
    FRParserIgnores()

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