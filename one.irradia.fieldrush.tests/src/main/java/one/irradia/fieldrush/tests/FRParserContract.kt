package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRAbstractParserArray
import one.irradia.fieldrush.api.FRAbstractParserObject
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserObjectFieldSchema
import one.irradia.fieldrush.api.FRParserObjectSchema
import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRValueParserProviderType
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
  abstract fun valueParsers(): FRValueParserProviderType
  abstract fun logger(): Logger

  private lateinit var parsers: FRParserProviderType
  private lateinit var valueParsers: FRValueParserProviderType
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
    this.valueParsers = this.valueParsers()
    this.logger = this.logger()
  }

  class IgnoreAll : FRAbstractParserObject<Unit>(onReceive = FRValueParsers.ignoringReceiverWithContext()) {
    override fun schema(context: FRParserContextType): FRParserObjectSchema {
      return FRParserObjectSchema(listOf())
    }

    override fun onCompleted(context: FRParserContextType): FRParseResult<Unit> {
      return FRParseResult.succeed(Unit)
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
      rootParser = FRValueParsers.forInteger()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals(BigInteger.valueOf(23), parsed)
    }
  }

  @Test
  fun testIntegerOKFlatMap0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-ok-0.json"),
      rootParser = FRValueParsers.forInteger()).use { parser ->
      val result = parser.parse().flatMap { x -> FRParseResult.succeed(x.toString()) }
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals("23", parsed)
    }
  }

  @Test
  fun testIntegerOKMap0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-ok-0.json"),
      rootParser = FRValueParsers.forInteger()).use { parser ->
      val result = parser.parse().map(BigInteger::toString)
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assert.assertEquals("23", parsed)
    }
  }

  @Test
  fun testIntegerBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-bad-0.json"),
      rootParser = FRValueParsers.forInteger()).use { parser ->
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
      rootParser = FRValueParsers.forBoolean()).use { parser ->
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
      rootParser = FRValueParsers.forBoolean()).use { parser ->
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
      rootParser = FRValueParsers.forBoolean()).use { parser ->
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
      rootParser = FRValueParsers.forReal()).use { parser ->
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
      rootParser = FRValueParsers.forReal()).use { parser ->
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
      rootParser = FRValueParsers.forURI()).use { parser ->
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
      rootParser = FRValueParsers.forURI()).use { parser ->
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
      rootParser = FRValueParsers.forScalar({ FRParseResult.succeed(Unit) }))
      .use { parser ->
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
      rootParser = FRValueParsers.forMIME()).use { parser ->
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
      rootParser = FRValueParsers.forMIME()).use { parser ->
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
      rootParser = FRValueParsers.forString()).use { parser ->
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

  class PointParser(
    onReceive: (FRParserContextType, Point) -> Unit = FRValueParsers.ignoringReceiverWithContext())
    : FRAbstractParserObject<Point>(onReceive) {

    private var x: BigInteger? = null
    private var y: BigInteger? = null
    private var z: BigInteger? = null

    override fun schema(context: FRParserContextType): FRParserObjectSchema =
      FRParserObjectSchema(listOf(
        FRParserObjectFieldSchema("x", { FRValueParsers.forInteger { i -> this.x = i } }),
        FRParserObjectFieldSchema("y", { FRValueParsers.forInteger { i -> this.y = i } }),
        FRParserObjectFieldSchema("z", { FRValueParsers.forInteger { i -> this.z = i } })))

    override fun onCompleted(context: FRParserContextType): FRParseResult<Point> =
      FRParseResult.succeed(Point(this.x!!, this.y!!, this.z!!))
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
        Assert.assertEquals(3, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Missing a required field 'x'"))
        Assert.assertThat(failed.errors[1].message, StringContains("Missing a required field 'y'"))
        Assert.assertThat(failed.errors[2].message, StringContains("Missing a required field 'z'"))
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

  class ArrayPointParser(
    onReceive: (FRParserContextType, List<Point>) -> Unit = FRValueParsers.ignoringReceiverWithContext()) :
    FRAbstractParserArray<Point>(onReceive) {
    private val points = mutableListOf<Point>()

    override fun onCompleted(context: FRParserContextType): FRParseResult<List<Point>> {
      return FRParseResult.succeed(this.points.toList())
    }

    override fun forIndex(context: FRParserContextType, index: Int): FRValueParserType<*>? {
      return PointParser(onReceive = { _, point -> this.points.add(point) })
    }
  }

  @Test
  fun testArrayPointOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = ArrayPointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assert.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assert.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assert.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assert.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assert.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assert.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assert.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assert.assertEquals(BigInteger.valueOf(9), parsed[2].z)
      }
  }

  @Test
  fun testArrayPointMonoOK1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = this.valueParsers.forArrayMonomorphic({ PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assert.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assert.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assert.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assert.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assert.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assert.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assert.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assert.assertEquals(BigInteger.valueOf(9), parsed[2].z)
      }
  }

  @Test
  fun testArrayPointBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-bad-0.json"),
      rootParser = ArrayPointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(3, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '{'"))
        Assert.assertThat(failed.errors[1].message, StringContains("Expected: '{'"))
        Assert.assertThat(failed.errors[2].message, StringContains("Expected: '{'"))
      }
  }

  @Test
  fun testArrayPointBad1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-bad-1.json"),
      rootParser = ArrayPointParser())
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '['"))
      }
  }

  @Test
  fun testArrayIntBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-int-bad-0.json"),
      rootParser = this.valueParsers.forArrayMonomorphic({ this.valueParsers.forInteger() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: A scalar value"))
      }
  }

  class ArraySkippingPointParser(
    onReceive: (FRParserContextType, List<Point>) -> Unit = FRValueParsers.ignoringReceiverWithContext(),
    val skip: Set<Int>) :
    FRAbstractParserArray<Point>(onReceive) {
    private val points = mutableListOf<Point>()

    override fun onCompleted(context: FRParserContextType): FRParseResult<List<Point>> {
      return FRParseResult.succeed(this.points.toList())
    }

    override fun forIndex(context: FRParserContextType, index: Int): FRValueParserType<*>? {
      return if (!this.skip.contains(index)) {
        PointParser(onReceive = { _, point -> this.points.add(point) })
      } else {
        null
      }
    }
  }

  @Test
  fun testArrayIgnoreFirstLast0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = ArraySkippingPointParser(skip = setOf(0, 2)))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(4), parsed[0].x)
        Assert.assertEquals(BigInteger.valueOf(5), parsed[0].y)
        Assert.assertEquals(BigInteger.valueOf(6), parsed[0].z)
      }
  }

  @Test
  fun testArrayOrSingleOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-0.json"),
      rootParser = this.valueParsers.forArrayOrSingle({ PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result[0]
        Assert.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assert.assertEquals(BigInteger.valueOf(100), parsed.y)
        Assert.assertEquals(BigInteger.valueOf(7), parsed.z)
      }
  }

  @Test
  fun testArrayOrSingleOK1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = this.valueParsers.forArrayOrSingle({ PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assert.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assert.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assert.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assert.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assert.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assert.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assert.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assert.assertEquals(BigInteger.valueOf(9), parsed[2].z)
      }
  }

  @Test
  fun testArrayOrSingleBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-int-ok-0.json"),
      rootParser = this.valueParsers.forArrayOrSingle({ PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(3, failed.errors.size)
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '{'"))
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '{'"))
        Assert.assertThat(failed.errors[0].message, StringContains("Expected: '{'"))
      }
  }

  @Test
  fun testFails() {
    resource("sources.txt").bufferedReader().useLines { lines ->
      for (name in lines) {
        this.parsers.createParser(
          uri = URI.create("urn:test"),
          stream = resource(name),
          rootParser = this.valueParsers.fails<Unit>())
          .use { parser ->
            val result = parser.parse()
            this.dumpParseResult(result)

            val failed = result as FRParseResult.FRParseFailed
            Assert.assertEquals(1, failed.errors.size)
          }
      }
    }
  }

  @Test
  fun testIgnores() {
    resource("sources.txt").bufferedReader().useLines { lines ->
      for (name in lines) {
        this.parsers.createParser(
          uri = URI.create("urn:test"),
          stream = resource(name),
          rootParser = this.valueParsers.ignores())
          .use { parser ->
            val result = parser.parse()
            this.dumpParseResult(result)

            val success = result as FRParseResult.FRParseSucceeded
            val parsed = success.result
          }
      }
    }
  }

  @Test
  fun testScalarObjectIntPointOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("integer-ok-0.json"),
      rootParser = this.valueParsers.forScalarOrObjectWithContext(
        forScalar = { this.valueParsers.forScalarWithContext(::scalarPoint) },
        forObject = { PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assert.assertEquals(BigInteger.valueOf(23), parsed.y)
        Assert.assertEquals(BigInteger.valueOf(23), parsed.z)
      }
  }

  @Test
  fun testScalarObjectIntPointOK1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-0.json"),
      rootParser = this.valueParsers.forScalarOrObjectWithContext(
        forScalar = { this.valueParsers.forScalarWithContext(::scalarPoint) },
        forObject = { PointParser() }))
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
  fun testScalarObjectArrayBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = this.valueParsers.forScalarOrObjectWithContext(
        forScalar = { this.valueParsers.forScalarWithContext(::scalarPoint) },
        forObject = { PointParser() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
      }
  }

  private fun scalarPoint(context: FRParserContextType, text: String): FRParseResult<Point> {
    return try {
      FRParseResult.succeed(Point(text.toBigInteger(), text.toBigInteger(), text.toBigInteger()))
    } catch (e: Exception) {
      context.failureOf(e.message ?: "Something failed", e)
    }
  }

  @Test
  fun testObjectMapOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-0.json"),
      rootParser = this.valueParsers.forObjectMap({ this.valueParsers.forInteger() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val success = result as FRParseResult.FRParseSucceeded
        val parsed = success.result
        Assert.assertEquals(BigInteger.valueOf(23), parsed["x"])
        Assert.assertEquals(BigInteger.valueOf(100), parsed["y"])
        Assert.assertEquals(BigInteger.valueOf(7), parsed["z"])
      }
  }

  @Test
  fun testObjectMapBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("array-point-ok-0.json"),
      rootParser = this.valueParsers.forObjectMap({ this.valueParsers.forInteger() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
      }
  }

  @Test
  fun testObjectMapBad1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-3.json"),
      rootParser = this.valueParsers.forObjectMap({ this.valueParsers.forInteger() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(3, failed.errors.size)
      }
  }

  @Test
  fun testObjectMapBad2() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-bad-4.json"),
      rootParser = this.valueParsers.forObjectMap({ this.valueParsers.forInteger() }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertEquals(1, failed.errors.size)
      }
  }
}
