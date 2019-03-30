package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserObjectSchema
import one.irradia.fieldrush.api.FRValueParserProviderType
import one.irradia.fieldrush.api.FRValueParserType
import one.irradia.mime.api.MIMEType
import one.irradia.mime.vanilla.MIMEParser
import java.math.BigInteger
import java.net.URI

/**
 * A convenient provider of value parsers.
 */

object FRValueParsers : FRValueParserProviderType {

  override fun <T> forObjectWithContext(
    onSchema: (FRParserContextType) -> FRParserObjectSchema,
    onCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> {
    return FRParserObject(receiver, onCompleted, onSchema)
  }

  override fun <T> forArrayMonomorphicWithContext(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> {
    return FRParserArrayMonomorphic(receiver, forEach)
  }

  override fun <T> forArrayWithContext(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> {
    return FRParserArray(receiver, onIndicesCompleted, forIndex)
  }

  override fun forIntegerWithContext(
    receiver: (FRParserContextType, BigInteger) -> Unit): FRValueParserType<BigInteger> {
    return FRValueParserInteger(receiver)
  }

  override fun forRealWithContext(
    receiver: (FRParserContextType, Double) -> Unit): FRValueParserType<Double> {
    return FRValueParserReal(receiver)
  }

  override fun forStringWithContext(
    receiver: (FRParserContextType, String) -> Unit): FRValueParserType<String> {
    return FRValueParserString(receiver)
  }

  override fun forMIMEWithContext(
    receiver: (FRParserContextType, MIMEType) -> Unit): FRValueParserType<MIMEType> {
    return FRValueParserMIME(onReceive = receiver, parsers = { text -> MIMEParser.create(text) })
  }

  override fun forBooleanWithContext(
    receiver: (FRParserContextType, Boolean) -> Unit): FRValueParserType<Boolean> {
    return FRValueParserBoolean(receiver)
  }

  override fun forURIWithContext(
    receiver: (FRParserContextType, URI) -> Unit): FRValueParserType<URI> {
    return FRValueParserURI(receiver)
  }

  override fun <T> forScalarWithContext(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> {
    return FRValueParserValid(receiver, validator)
  }
}

