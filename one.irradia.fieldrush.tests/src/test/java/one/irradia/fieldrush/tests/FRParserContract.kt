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
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.io.FileNotFoundException
import java.io.InputStream
import java.math.BigInteger
import java.net.URI

abstract class FRParserContract {

  abstract fun parsers(): FRParserProviderType
  abstract fun valueParsers(): FRValueParserProviderType
  abstract fun logger(): Logger

  private lateinit var savedTimeZone: DateTimeZone
  private lateinit var parsers: FRParserProviderType
  private lateinit var valueParsers: FRValueParserProviderType
  private lateinit var logger: Logger

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

  @BeforeEach
  fun testSetup() {
    this.parsers = this.parsers()
    this.valueParsers = this.valueParsers()
    this.logger = this.logger()
    this.savedTimeZone = DateTimeZone.getDefault()
  }

  @AfterEach
  fun testTearDown() {
    DateTimeZone.setDefault(this.savedTimeZone)
  }

  class IgnoreAll : FRAbstractParserObject<Unit>(onReceive = FRValueParsers.ignoringReceiverWithContext()) {
    override fun schema(context: FRParserContextType): FRParserObjectSchema {
      return FRParserObjectSchema(listOf())
    }

    override fun onCompleted(context: FRParserContextType): FRParseResult<Unit> {
      return FRParseResult.succeed(warnings = listOf(), Unit)
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
      Assertions.assertEquals(BigInteger.valueOf(23), parsed)
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
      Assertions.assertEquals("23", parsed)
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
      Assertions.assertEquals("23", parsed)
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
      Assertions.assertTrue(failed.errors[0].message.contains("An integer"))
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
      Assertions.assertEquals(true, parsed)
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
      Assertions.assertEquals(false, parsed)
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
      Assertions.assertTrue(failed.errors[0].message.contains("A boolean"))
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
      Assertions.assertEquals(23.0, parsed, 0.0)
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
      Assertions.assertTrue(failed.errors[0].message.contains("A floating-point"))
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
      Assertions.assertEquals(URI.create("http://www.example.com"), parsed)
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
      Assertions.assertTrue(failed.errors[0].message.contains("A valid URI"))
    }
  }

  @Test
  fun testScalarBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("not-scalar-0.json"),
      rootParser = FRValueParsers.forScalar({ FRParseResult.succeed(warnings = listOf(), Unit) }))
      .use { parser ->
        val result = parser.parse()
        this.dumpParseResult(result)

        val failed = result as FRParseResult.FRParseFailed
        Assertions.assertTrue(failed.errors[0].message.contains("A scalar value"))
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
      Assertions.assertEquals(MIMEParser.parseRaisingException("text/plain"), parsed)
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
      Assertions.assertTrue(failed.errors[0].message.contains("A valid MIME"))
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
      Assertions.assertEquals("xyz", parsed)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assertions.assertEquals(BigInteger.valueOf(100), parsed.y)
        Assertions.assertEquals(BigInteger.valueOf(7), parsed.z)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assertions.assertEquals(BigInteger.valueOf(24), parsed.y)
        Assertions.assertEquals(BigInteger.valueOf(25), parsed.z)
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
        Assertions.assertEquals(3, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("An integer"))
        Assertions.assertTrue(failed.errors[1].message.contains("An integer"))
        Assertions.assertTrue(failed.errors[2].message.contains("An integer"))
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
        Assertions.assertEquals(3, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Missing a required field 'x'"))
        Assertions.assertTrue(failed.errors[1].message.contains("Missing a required field 'y'"))
        Assertions.assertTrue(failed.errors[2].message.contains("Missing a required field 'z'"))
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
        Assertions.assertEquals(1, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: '{'"))
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
        Assertions.assertEquals(3, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: A scalar value"))
        Assertions.assertTrue(failed.errors[1].message.contains("Expected: A scalar value"))
        Assertions.assertTrue(failed.errors[2].message.contains("Expected: A scalar value"))
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
        Assertions.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assertions.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assertions.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assertions.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assertions.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assertions.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assertions.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assertions.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assertions.assertEquals(BigInteger.valueOf(9), parsed[2].z)
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
        Assertions.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assertions.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assertions.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assertions.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assertions.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assertions.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assertions.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assertions.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assertions.assertEquals(BigInteger.valueOf(9), parsed[2].z)
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
        Assertions.assertEquals(3, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: '{'"))
        Assertions.assertTrue(failed.errors[1].message.contains("Expected: '{'"))
        Assertions.assertTrue(failed.errors[2].message.contains("Expected: '{'"))
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
        Assertions.assertEquals(1, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: '['"))
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
        Assertions.assertEquals(1, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: A scalar value"))
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
        Assertions.assertEquals(BigInteger.valueOf(4), parsed[0].x)
        Assertions.assertEquals(BigInteger.valueOf(5), parsed[0].y)
        Assertions.assertEquals(BigInteger.valueOf(6), parsed[0].z)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assertions.assertEquals(BigInteger.valueOf(100), parsed.y)
        Assertions.assertEquals(BigInteger.valueOf(7), parsed.z)
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
        Assertions.assertEquals(BigInteger.valueOf(1), parsed[0].x)
        Assertions.assertEquals(BigInteger.valueOf(2), parsed[0].y)
        Assertions.assertEquals(BigInteger.valueOf(3), parsed[0].z)

        Assertions.assertEquals(BigInteger.valueOf(4), parsed[1].x)
        Assertions.assertEquals(BigInteger.valueOf(5), parsed[1].y)
        Assertions.assertEquals(BigInteger.valueOf(6), parsed[1].z)

        Assertions.assertEquals(BigInteger.valueOf(7), parsed[2].x)
        Assertions.assertEquals(BigInteger.valueOf(8), parsed[2].y)
        Assertions.assertEquals(BigInteger.valueOf(9), parsed[2].z)
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
        Assertions.assertEquals(3, failed.errors.size)
        Assertions.assertTrue(failed.errors[0].message.contains("Expected: '{'"))
        Assertions.assertTrue(failed.errors[1].message.contains("Expected: '{'"))
        Assertions.assertTrue(failed.errors[2].message.contains("Expected: '{'"))
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
            Assertions.assertEquals(1, failed.errors.size)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.y)
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.z)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed.x)
        Assertions.assertEquals(BigInteger.valueOf(100), parsed.y)
        Assertions.assertEquals(BigInteger.valueOf(7), parsed.z)
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
        Assertions.assertEquals(1, failed.errors.size)
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
        Assertions.assertEquals(BigInteger.valueOf(23), parsed["x"])
        Assertions.assertEquals(BigInteger.valueOf(100), parsed["y"])
        Assertions.assertEquals(BigInteger.valueOf(7), parsed["z"])
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
        Assertions.assertEquals(1, failed.errors.size)
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
        Assertions.assertEquals(3, failed.errors.size)
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
        Assertions.assertEquals(1, failed.errors.size)
      }
  }

  @Test
  fun testTimestampOK0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("timestamp-ok-0.json"),
      rootParser = FRValueParsers.forTimestamp()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(Instant.parse("2010-01-01T00:00:10"), parsed)
    }
  }

  @Test
  fun testTimestampBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("mime-ok-0.json"),
      rootParser = FRValueParsers.forTimestamp()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assertions.assertEquals(1, failed.errors.size)
    }
  }

  @Test
  fun testTimestampBad1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("string-ok-0.json"),
      rootParser = FRValueParsers.forTimestamp()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assertions.assertEquals(1, failed.errors.size)
    }
  }

  @Test
  fun testTimestampBad2() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("object-point-ok-0.json"),
      rootParser = FRValueParsers.forTimestamp()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assertions.assertEquals(1, failed.errors.size)
    }
  }

  @Test
  fun testScalarNullAccepted0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("string-ok-0.json"),
      rootParser = FRValueParsers.forScalarOrNull({ s -> FRParseResult.succeed(s) })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals("xyz", parsed)
    }
  }

  @Test
  fun testScalarNullAccepted1() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("null.json"),
      rootParser = FRValueParsers.forScalarOrNull({ s -> FRParseResult.succeed(s) })).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(null, parsed)
    }
  }

  @Test
  fun testDateTimeBad0() {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("mime-ok-0.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val failed = result as FRParseResult.FRParseFailed
      Assertions.assertEquals(1, failed.errors.size)
    }
  }

  @Test
  fun testDateTimeOK0_UTC() {
    DateTimeZone.setDefault(DateTimeZone.UTC)

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-0.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10"), parsed)
    }
  }

  @Test
  fun testDateTimeOK0_UTC_Plus5() {
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(5))

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-0.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10Z"), parsed)
    }
  }

  @Test
  fun testDateTimeOK1_UTC() {
    DateTimeZone.setDefault(DateTimeZone.UTC)

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-1.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10"), parsed)
    }
  }

  @Test
  fun testDateTimeOK1_UTC_Plus5() {
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(5))

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-1.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10Z"), parsed)
    }
  }

  @Test
  fun testDateTimeOK2_UTC() {
    DateTimeZone.setDefault(DateTimeZone.UTC)

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-2.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10"), parsed)
    }
  }

  @Test
  fun testDateTimeOK2_UTC_Plus5() {
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(5))

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-2.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T00:00:10Z"), parsed)
    }
  }

  @Test
  fun testDateTimeOK3_UTC() {
    DateTimeZone.setDefault(DateTimeZone.UTC)

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-3.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T05:00:10"), parsed)
    }
  }

  @Test
  fun testDateTimeOK3_UTC_Minus5() {
    DateTimeZone.setDefault(DateTimeZone.forOffsetHours(-5))

    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = resource("datetime-ok-3.json"),
      rootParser = FRValueParsers.forDateTimeUTC()).use { parser ->
      val result = parser.parse()
      this.dumpParseResult(result)

      val success = result as FRParseResult.FRParseSucceeded
      val parsed = success.result
      Assertions.assertEquals(DateTime.parse("2010-01-01T05:00:10Z"), parsed)
    }
  }
}
