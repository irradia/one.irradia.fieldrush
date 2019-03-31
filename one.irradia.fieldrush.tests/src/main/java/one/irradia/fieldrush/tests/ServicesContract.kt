package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRParserProviderType
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import java.util.ServiceLoader

abstract class ServicesContract {

  abstract fun logger(): Logger

  @Test
  fun testFRParserProviderType() {
    val loader =
      ServiceLoader.load(FRParserProviderType::class.java)
    val services =
      loader.iterator().asSequence().toList()

    val logger = this.logger()
    services.forEach { service -> logger.debug("service: {}", service) }
    Assert.assertTrue("At least one service exists", services.size >= 1)
  }
}