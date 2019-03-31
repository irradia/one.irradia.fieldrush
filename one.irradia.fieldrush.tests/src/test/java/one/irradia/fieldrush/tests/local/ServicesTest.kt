package one.irradia.fieldrush.tests.local

import one.irradia.fieldrush.tests.ServicesContract
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServicesTest : ServicesContract() {

  override fun logger(): Logger {
    return LoggerFactory.getLogger(ServicesTest::class.java)
  }

}