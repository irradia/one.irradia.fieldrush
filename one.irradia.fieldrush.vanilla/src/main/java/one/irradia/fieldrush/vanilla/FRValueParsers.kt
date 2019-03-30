package one.irradia.fieldrush.vanilla

import one.irradia.fieldrush.api.FRAbstractParserArray
import one.irradia.fieldrush.api.FRAbstractParserObject
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

    return object: FRAbstractParserObject<T>() {
      override fun onFieldsCompleted(context: FRParserContextType): FRParseResult<T> {
        return onFieldsCompleted(context)
      }

      override fun forField(context: FRParserContextType, name: String): FRValueParserType<*>? {
        return forField(context, name)
      }

      override fun receive(context: FRParserContextType, result: T) {
        return receiver.invoke(context, result)
      }
    }
  }

  override fun <T> forArray(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>> {

    return object: FRAbstractParserArray<T>() {
      override fun onIndicesCompleted(context: FRParserContextType): FRParseResult<List<T>> {
        return onIndicesCompleted(context)
      }

      override fun forIndex(context: FRParserContextType, index: Int): FRValueParserType<*>? {
        return forIndex(context, index)
      }

      override fun receive(context: FRParserContextType, result: List<T>) {
        return receiver.invoke(context, result)
      }
    }
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
    return object: FRValueParserScalar<T>(receiver), FRValueParserType<T> {
      override fun ofText(context: FRParserContextType, text: String): FRParseResult<T> {
        return validator.invoke(context, text)
      }
      override fun receive(context: FRParserContextType, result: T) {
        return receiver.invoke(context, result)
      }
    }
  }
}
