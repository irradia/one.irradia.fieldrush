package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRAbstractParserObject
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRValueParserType
import one.irradia.fieldrush.vanilla.FRValueParsers
import one.irradia.mime.vanilla.MIMEParser
import org.hamcrest.core.StringContains
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.slf4j.Logger
import java.io.FileNotFoundException
import java.io.InputStream
import java.math.BigInteger
import java.net.URI

abstract class FRParserContract {

  abstract fun parsers(): FRParserProviderType

  abstract fun logger(): Logger

  private lateinit var parsers: FRParserProviderType
  private lateinit var logger: Logger

  @JvmField
  @Rule
  val expected = ExpectedException.none()

  private fun resource(name: String): InputStream {
    val path = "/one/irradia/fieldrush/tests/$name"
    val url =
      FRParserContract::class.java.getResource(path)
        ?: throw FileNotFoundException("No such resource: $path")
    return url.openStream()
  }

  private fun <T> dumpParseResult(result: FRParseResult<T>) {
    return when (result) {
      is FRParseResult.FRParseSucceeded -> {
        this.logger.debug("success: {}", result.result)
      }
      is FRParseResult.FRParseFailed -> {
        result.errors.forEach { error ->
          this.logger.debug("error: {}: ", error, error.exception)
        }
      }
    }
  }

  @Before
  fun testSetup() {
    this.parsers = this.parsers()
    this.logger = this.logger()
  }

  class IgnoreAll : FRAbstractParserObject<Unit>() {
    override fun onFieldsCompleted(context: FRParserContextType): FRParseResult<Unit> {
      return FRParseResult.succeed(Unit)
    }

    override fun forField(context: FRParserContextType, name: String): FRValueParserType<*>? {
      return null
    }

    override fun receive(context: FRParserContextType, result: Unit) {

    }
  }

  @Test
  fun testEmpty() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("empty.json"),
      rootParser = IgnoreAll()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
    }
  }

  @Test
  fun testIntegerOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-ok-0.json"),
      rootParser = FRValueParsers.forInteger(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(BigInteger.valueOf(23), parsed)
    }
  }

  @Test
  fun testIntegerBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-bad-0.json"),
      rootParser = FRValueParsers.forInteger(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("An integer"))
    }
  }

  @Test
  fun testBooleanOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("boolean-ok-0.json"),
      rootParser = FRValueParsers.forBoolean(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(true, parsed)
    }
  }

  @Test
  fun testBooleanOK1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("boolean-ok-1.json"),
      rootParser = FRValueParsers.forBoolean(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(false, parsed)
    }
  }

  @Test
  fun testBooleanBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("boolean-bad-0.json"),
      rootParser = FRValueParsers.forBoolean(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("A boolean"))
    }
  }

  @Test
  fun testRealOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("real-ok-0.json"),
      rootParser = FRValueParsers.forReal(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(23.0, parsed, 0.0)
    }
  }

  @Test
  fun testRealBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("real-bad-0.json"),
      rootParser = FRValueParsers.forReal(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("A floating-point"))
    }
  }

  @Test
  fun testURIOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("uri-ok-0.json"),
      rootParser = FRValueParsers.forURI(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(URI.create("http://www.example.com"), parsed)
    }
  }

  @Test
  fun testURIBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("uri-bad-0.json"),
      rootParser = FRValueParsers.forURI(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("A valid URI"))
    }
  }

  @Test
  fun testScalarBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("not-scalar-0.json"),
      rootParser = FRValueParsers.forScalar(
        validator = { _, _ -> FRParseResult.succeed(Unit) },
        receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("A scalar value"))
    }
  }

  @Test
  fun testMIMEOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("mime-ok-0.json"),
      rootParser = FRValueParsers.forMIME(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(MIMEParser.parseRaisingException("text/plain"), parsed)
    }
  }

  @Test
  fun testMIMEBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("mime-bad-0.json"),
      rootParser = FRValueParsers.forMIME(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assert.assertThat(failed.errors[0].message, StringContains("A valid MIME"))
    }
  }

  @Test
  fun testStringOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("string-ok-0.json"),
      rootParser = FRValueParsers.forString(receiver = { _, _ -> })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals("xyz", parsed)
    }
  }

  data class Point(
    val x: BigInteger,
    val y: BigInteger,
    val z: BigInteger)

  class PointParser: FRAbstractParserObject<Point>() {
    private var x: BigInteger? = null
    private var y: BigInteger? = null
    private var z: BigInteger? = null

    override fun onFieldsCompleted(context: FRParserContextType): FRParseResult<Point> {
      val vx = x
      val vy = y
      val vz = z
      return if (vx != null && vy != null && vz != null) {
        FRParseResult.succeed(Point(vx, vy, vz))
      } else {
        context.failureOf("Missing one or more required fields (x, y, or z)")
      }
    }

    override fun forField(context: FRParserContextType, name: String): FRValueParserType<*>? {
      return when (name) {
        "x" -> FRValueParsers.forInteger { _, value -> this.x = value }
        "y" -> FRValueParsers.forInteger { _, value -> this.y = value }
        "z" -> FRValueParsers.forInteger { _, value -> this.z = value }
        else -> null
      }
    }

    override fun receive(context: FRParserContextType, result: Point) {

    }
  }

  @Test
  fun testObjectPointOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-0.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assert.assertEquals(BigInteger.valueOf(100), parsed.y)
        Assert.assertEquals(BigInteger.valueOf(7), parsed.z)
      }
  }

  @Test
  fun testObjectPointOK1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-1.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assert.assertEquals(BigInteger.valueOf(24), parsed.y)
        Assert.assertEquals(BigInteger.valueOf(25), parsed.z)
      }
  }

  @Test
  fun testObjectPointBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-0.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(3, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("An integer"))
        Assert.assertThat(failed.errors[1].message, StringContains("An integer"))
        Assert.assertThat(failed.errors[2].message, StringContains("An integer"))
      }
  }

  @Test
  fun testObjectPointBad1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-1.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Missing one or more required fields"))
      }
  }

  @Test
  fun testObjectPointBad2() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-2.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '{'"))
      }
  }

  @Test
  fun testObjectPointBad3() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-3.json"),
      rootParser = PointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(3, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: A scalar value"))
        Assert.assertThat(failed.errors[1].message, StringContains("Expected: A scalar value"))
        Assert.assertThat(failed.errors[2].message, StringContains("Expected: A scalar value"))
      }
  }
}
