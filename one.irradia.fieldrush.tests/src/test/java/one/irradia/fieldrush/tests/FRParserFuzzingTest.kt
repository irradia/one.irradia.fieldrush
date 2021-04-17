package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRValueParserProviderType
import one.irradia.fieldrush.vanilla.FRParsers
import one.irradia.fieldrush.vanilla.FRValueParsers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FRParserFuzzingTest : FRParserFuzzing() {
  override fun valueParsers(): FRValueParserProviderType {
    return FRValueParsers
  }

  override fun parsers(): FRParserProviderType {
    return FRParsers()
  }

  override fun logger(): Logger {
    return LoggerFactory.getLogger(FRParserTest::class.java)
  }
}
