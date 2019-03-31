package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRAbstractParserObject
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParserContextType
import one.irradia.fieldrush.api.FRParserIgnores
import one.irradia.fieldrush.api.FRParserObjectSchema
import one.irradia.fieldrush.api.FRParserProviderType
import one.irradia.fieldrush.api.FRValueParserProviderType
import one.irradia.fieldrush.vanilla.FRValueParsers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.slf4j.Logger
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.util.Random
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class FRParserFuzzing {

  abstract fun parsers(): FRParserProviderType
  abstract fun valueParsers(): FRValueParserProviderType
  abstract fun logger(): Logger

  private lateinit var parsers: FRParserProviderType
  private lateinit var valueParsers: FRValueParserProviderType
  private lateinit var logger: Logger
  private lateinit var executor: ExecutorService

  @JvmField
  @Rule
  val expected = ExpectedException.none()

  @Before
  fun testSetup() {
    this.parsers = this.parsers()
    this.valueParsers = this.valueParsers()
    this.logger = this.logger()
    this.executor = Executors.newSingleThreadExecutor()
  }

  @After
  fun testTearDown()
  {
    this.executor.shutdown()
  }

  private fun resourceBytes(name: String): ByteArray {
    val path = "/one/irradia/fieldrush/tests/$name"
    val url =
      FRParserContract::class.java.getResource(path)
        ?: throw FileNotFoundException("No such resource: $path")
    val data = url.openStream().readBytes()
    return data
  }

  private fun corruptResource(name:String, seed:Long): InputStream {
    val data = resourceBytes(name)
    val rng = Random(seed)
    val chars = listOf('[', ']', '{', '}', ',')

    for (index in 0 until data.size) {
      if (rng.nextDouble() < 0.002) {
        data[index] = chars[rng.nextInt(chars.size)].toByte()
      }
    }

    val tmpdir = System.getProperty("java.io.tmpdir")
    if (tmpdir != null) {
      val file = File("${tmpdir}/frparser-fuzz-${seed.toString(16)}")
      FileOutputStream(file).use { stream ->
        stream.write(data)
        stream.flush()
      }
    }

    return ByteArrayInputStream(data)
  }

  class VisitAll(val valueParsers: FRValueParserProviderType)
    : FRAbstractParserObject<Unit>(onReceive = FRValueParsers.ignoringReceiverWithContext()) {

    override fun schema(context: FRParserContextType): FRParserObjectSchema {
      return FRParserObjectSchema(
        fields = listOf(),
        unknownField = { _,_ ->
          this.valueParsers.forScalarArrayOrObject(
            forScalar = { FRParserIgnores() },
            forArray = { this.valueParsers.forArrayMonomorphic({ FRParserIgnores() }) },
            forObject = { VisitAll(valueParsers) })
        })
    }

    override fun onCompleted(context: FRParserContextType): FRParseResult<Unit> {
      return FRParseResult.succeed(Unit)
    }
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

  @Test
  fun testFuzz() {
    listOf(
      0xc5a6b0755b00,
      0xa6440de12938,
      0xede6271ac30b,
      0x8cdd920e274b,
      0xbbd9540c00bf,
      0x6c558a30d869,
      0x2639c67bb7a5,
      0x080ea1d05640,
      0xf64a6bd75fb2,
      0xe1835ba1e610,
      0x107b29d8dd32,
      0x6a456db5bcdd,
      0xdf3e0cf0394d,
      0xf4d6cf507b7d,
      0xdac078712adc,
      0x55e347f122fb,
      0x7ab27f253dbc,
      0x860742d71c92,
      0x557006723709,
      0xcce2303c6581,
      0xf84e8203aeef,
      0x8f72c3a80b13,
      0x992b61cfa9e4,
      0x238404534897,
      0xf2b1ab4954a4,
      0x4dde48d505d6,
      0xe1019e7d1f65,
      0x14ede42503b0,
      0xe25f2f30c5e3,
      0x353ba8e5a28e,
      0x22f9c9356883,
      0x8e9a5625d168,
      0xe275d1c499c2,
      0x2f0ee00036b6,
      0x6dd930c41b93,
      0x46e262f1e806,
      0xe23cc97f8fe5,
      0xb967fcf4f912,
      0x2ca883edfdda,
      0x4bebf42e98e5,
      0x9af9e6d7cf73,
      0xb972c2c3f75e,
      0xa9b41f1e9fee,
      0x2e831523cbd7,
      0xeb4b69e322c7,
      0x836151d77f45,
      0xc46864c9b560,
      0x4fe975a6d49e,
      0x07fabd084ae0,
      0x0ea3746e91e6,
      0x4343e84820f1,
      0xe54ac8c6cbe0,
      0xf338f7aa3d5d,
      0x5b0e16b12d80,
      0x3ec6415592df,
      0x75e92e2f6009,
      0x81ead0c77ac9,
      0x8d9778a6e43a,
      0x9d630ec26540,
      0xebb9d8684f15,
      0x9b5be9c1c3cb,
      0x687090a384d1,
      0x3cb5e899f906,
      0x806610b444b9,
      0xd96aac4bb698,
      0x0a85115b911c,
      0x13132efbed4d,
      0x6d56ce27c7bc,
      0xa6e5d2147063,
      0x7b8ca6c54076,
      0x850652f8fc74,
      0x075fdb51b574,
      0x50835ad52658,
      0xc5a6b0755b00,
      0x1c89e8f20c6e,
      0xe9aa55a460d8,
      0x220c86814099,
      0xbb004f5b3ba2,
      0x3d8db8217bce,
      0x72d9fe6dfd12,
      0x1925e0671a22,
      0xeb0c1054ab61,
      0xab109117900d,
      0x879994addafd,
      0xdc7a5be1dced,
      0xfed6cdcb614a,
      0x08c1f88c4354,
      0x8e908f399a81,
      0x0ccaa598f86a,
      0xed6e2f63bd8f,
      0xf4e95789bee4,
      0x3bdc6f64a4ef,
      0x85fb09dc7a8c,
      0x96d7faa4901f,
      0x18814972cf05,
      0x53d603b4ce8c,
      0xda370a07715f,
      0xffcab0b4d8df,
      0x4b980a22de08,
      0x1fa14bccc0db,
      0x733f924bb87b,
      0xfe64f8b35d34)
      .forEach { seed -> fuzz("gardeur-test-catalog-0.json", seed) }
  }

  private fun fuzz(name: String, seed: Long) {
    this.parsers.createParser(
      uri = URI.create("urn:test"),
      stream = corruptResource(name, seed),
      rootParser = VisitAll(this.valueParsers))
      .use { parser ->

        val future = this.executor.submit(Callable{ parser.parse() })
        val result = future.get(10L, TimeUnit.SECONDS)

        this.dumpParseResult(result)
        this.logger.debug("result for: 0x{}", seed.toString(16))
        Assert.assertTrue(
          "fuzz ${seed.toString(16)} should have failed",
          result is FRParseResult.FRParseFailed)

        val failed = result as FRParseResult.FRParseFailed
        Assert.assertTrue(
          "fuzz ${seed.toString(16)} should have failed",
          failed.errors.size >= 1)
      }
  }

}
