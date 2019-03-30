package one.irradia.fieldrush.tests.local

import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.tests.FRParserContract
import one.irradia.fieldrush.vanilla.FRParsers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FRParserTest : FRParserContract() {
  override fun parsers(): FRParserProviderType {
    return FRParsers()
  }

  override fun logger(): Logger {
    return LoggerFactory.getLogger(FRParserTest::class.java)
  }
}