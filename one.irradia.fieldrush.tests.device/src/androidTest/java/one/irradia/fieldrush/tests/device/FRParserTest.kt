package one.irradia.fieldrush.tests.device

import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRValueParserProviderType
import one.irradia.fieldrush.tests.FRParserContract
import one.irradia.fieldrush.vanilla.FRParsers
import one.irradia.fieldrush.vanilla.FRValueParsers
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(AndroidJUnit4::class)
@MediumTest
class FRParserTest : FRParserContract() {

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