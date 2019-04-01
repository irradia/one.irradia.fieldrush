package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserArrayOrSingleType
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserObjectMapType
import one.irradia.fieldrush.api.FRParserObjectSchema
import one.irradia.fieldrush.api.FRParserScalarArrayOrObjectType
import one.irradia.fieldrush.api.FRParserScalarOrObjectType
import one.irradia.fieldrush.api.FRValueParserProviderType
import one.irradia.fieldrush.api.FRValueParserType
import one.irradia.mime.api.MIMEType
import one.irradia.mime.vanilla.MIMEParser
import org.joda.time.Instant
import java.math.BigInteger
import java.net.URI

/**
 * A convenient provider of value parsers.
 */

object FRValueParsers : FRValueParserProviderType {

  override fun forTimestampWithContext(
    receiver: (FRParserContextType, Instant) -> Unit): FRValueParserType<Instant> =
    FRValueParserTimestamp(receiver)

  override fun <T> forObjectMapWithContext(
    forKey: (FRParserContextType, String) -> FRValueParserType<T>,
    receiver: (FRParserContextType, Map<String, T>) -> Unit): FRParserObjectMapType<T> =
    FRParserObjectMap(forKey, receiver)

  override fun <T> forArrayOrSingleWithContext(
    forItem: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRParserArrayOrSingleType<T> =
    FRParserArrayOrSingle(receiver, forItem)

  override fun <T> forScalarOrObjectWithContext(
    forScalar: (FRParserContextType) -> FRValueParserType<T>,
    forObject: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, T) -> Unit): FRParserScalarOrObjectType<T> =
    FRParserScalarOrObject(receiver, forScalar, forObject)

  override fun <T> forScalarArrayOrObjectWithContext(
    forScalar: (FRParserContextType) -> FRValueParserType<T>,
    forArray: (FRParserContextType) -> FRValueParserType<List<T>>,
    forObject: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRParserScalarArrayOrObjectType<T> =
    FRParserScalarArrayOrObject(receiver, forScalar, forArray, forObject)

  override fun <T> forObjectWithContext(
    onSchema: (FRParserContextType) -> FRParserObjectSchema,
    onCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> =
    FRParserObject(receiver, onCompleted, onSchema)

  override fun <T> forArrayMonomorphicWithContext(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> =
    FRParserArrayMonomorphic(receiver, forEach)

  override fun <T> forArrayWithContext(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> =
    FRParserArray(receiver, onIndicesCompleted, forIndex)

  override fun forIntegerWithContext(
    receiver: (FRParserContextType, BigInteger) -> Unit): FRValueParserType<BigInteger> =
    FRValueParserInteger(receiver)

  override fun forRealWithContext(
    receiver: (FRParserContextType, Double) -> Unit): FRValueParserType<Double> =
    FRValueParserReal(receiver)

  override fun forStringWithContext(
    receiver: (FRParserContextType, String) -> Unit): FRValueParserType<String> =
    FRValueParserString(receiver)

  override fun forMIMEWithContext(
    receiver: (FRParserContextType, MIMEType) -> Unit): FRValueParserType<MIMEType> =
    FRValueParserMIME(onReceive = receiver, parsers = { text -> MIMEParser.create(text) })

  override fun forBooleanWithContext(
    receiver: (FRParserContextType, Boolean) -> Unit): FRValueParserType<Boolean> =
    FRValueParserBoolean(receiver)

  override fun forURIWithContext(
    receiver: (FRParserContextType, URI) -> Unit): FRValueParserType<URI> =
    FRValueParserURI(receiver)

  override fun <T> forScalarWithContext(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> =
    FRValueParserValid(receiver, validator)
}

