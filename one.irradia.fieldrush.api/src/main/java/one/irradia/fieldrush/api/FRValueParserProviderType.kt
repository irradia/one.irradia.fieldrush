package one.irradia.fieldrush.api

import one.irradia.mime.api.MIMEType
import java.math.BigInteger
import java.net.URI

interface FRValueParserProviderType {

  fun forInteger(
    receiver: (FRParserContextType, BigInteger) -> Unit): FRValueParserType<BigInteger>

  fun forReal(
    receiver: (FRParserContextType, Double) -> Unit): FRValueParserType<Double>

  fun forString(
    receiver: (FRParserContextType, String) -> Unit): FRValueParserType<String>

  fun forMIME(
    receiver: (FRParserContextType, MIMEType) -> Unit): FRValueParserType<MIMEType>

  fun forBoolean(
    receiver: (FRParserContextType, Boolean) -> Unit): FRValueParserType<Boolean>

  fun forURI(
    receiver: (FRParserContextType, URI) -> Unit): FRValueParserType<URI>

  fun <T> forScalar(
    validator: (FRParserContextType, String) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T>

  fun <T> forObject(
    forField: (FRParserContextType, String) -> FRValueParserType<*>,
    onFieldsCompleted: (FRParserContextType) -> FRParseResult<T>,
    receiver: (FRParserContextType, T) -> Unit): FRValueParserType<T>

  fun <T> forArray(
    forIndex: (FRParserContextType, Int) -> FRValueParserType<*>,
    onIndicesCompleted: (FRParserContextType) -> FRParseResult<List<T>>,
    receiver: (FRParserContextType, List<T>) -> Unit): FRValueParserType<List<T>>

}