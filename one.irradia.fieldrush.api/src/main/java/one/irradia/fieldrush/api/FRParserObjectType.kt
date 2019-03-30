package one.irradia.fieldrush.api

interface FRParserObjectType<T> : FRValueParserType<T> {

  fun onFieldsCompleted(
    context: FRParserContextType)
    : FRParseResult<T>

  fun forField(
    context: FRParserContextType,
    name: String)
    : FRValueParserType<*>?

}
