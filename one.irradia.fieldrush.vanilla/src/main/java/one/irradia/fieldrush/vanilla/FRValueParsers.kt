package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
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

  override fun <T> forObject(
    forField: (FRParserContextType, String) -> FRValueParserType<*>,
    onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> {
    return FRParserObject(receiver, onFieldsCompleted, forField)
  }

  override fun <T> forArrayMonomorphic(
    forEach: (FRParserContextType) -> FRValueParserType<T>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> {
    return FRParserArrayMonomorphic(receiver, forEach)
  }

  override fun <T> forArray(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> {
    return FRParserArray(receiver, onIndicesCompleted, forIndex)
  }

  override fun forInteger(
    receiver: (FRParserContextType, BigInteger) -> Unit): FRValueParserType<BigInteger> {
    return FRValueParserInteger(receiver)
  }

  override fun forReal(
    receiver: (FRParserContextType, Double) -> Unit): FRValueParserType<Double> {
    return FRValueParserReal(receiver)
  }

  override fun forString(
    receiver: (FRParserContextType, String) -> Unit): FRValueParserType<String> {
    return FRValueParserString(receiver)
  }

  override fun forMIME(
    receiver: (FRParserContextType, MIMEType) -> Unit): FRValueParserType<MIMEType> {
    return FRValueParserMIME(onReceive = receiver, parsers = { text -> MIMEParser.create(text) })
  }

  override fun forBoolean(
    receiver: (FRParserContextType, Boolean) -> Unit): FRValueParserType<Boolean> {
    return FRValueParserBoolean(receiver)
  }

  override fun forURI(
    receiver: (FRParserContextType, URI) -> Unit): FRValueParserType<URI> {
    return FRValueParserURI(receiver)
  }

  override fun <T> forScalar(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T> {
    return FRValueParserValid(receiver, validator)
  }
}

