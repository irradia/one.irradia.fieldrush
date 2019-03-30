package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.Companion.succeed
import org.junit.Assert
import org.junit.Test

abstract class FRParseResultContract {

  /**
   * return a >>= f ? f a
   */

  @Test
  fun testFlatMapLeftIdentity() {
    val a = 23
    val m = FRParseResult.succeed(a)
    val f = { y: Int -> FRParseResult.FRParseSucceeded(y * 2) }
    Assert.assertEquals(m.flatMap(f), f(a))
  }

  /**
   * m >>= return ? m
   */

  @Test
  fun testFlatMapRightIdentity() {
    val m = FRParseResult.succeed(23)
    val r = m.flatMap { y -> succeed(y) }
    Assert.assertEquals(m, r)
  }

  /**
   * (m >>= f) >>= g ? m >>= (\x -> f x >>= g)
   */

  @Test
  fun testFlatMapAssociative() {
    val m = FRParseResult.succeed(23)
    val f = { y: Int -> succeed(y * 2) }
    val g = { y: Int -> succeed(y * 3) }

    Assert.assertEquals(
      m.flatMap(f).flatMap(g),
      m.flatMap({ x -> f(x).flatMap(g) }))
  }

  /**
   * map(f, return a) == return (f a)
   */

  @Test
  fun testMap() {
    val a = 23
    val f = Any::toString
    val m = FRParseResult.succeed(a).map(f)
    val n = FRParseResult.succeed(f(a))
    Assert.assertEquals(m, n)
  }
}