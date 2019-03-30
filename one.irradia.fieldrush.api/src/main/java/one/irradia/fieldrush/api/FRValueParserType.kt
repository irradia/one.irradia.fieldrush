package one.irradia.fieldrush.api

interface FRValueParserType<T> {

  fun parse(context: FRParserContextType): FRParseResult<T>

  fun receive(context: FRParserContextType, result: T)

}
