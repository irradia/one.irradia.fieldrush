package one.irradia.fieldrush.tests

import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.Companion.succeed
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


abstract class FRParseResultContract {

  /**
   * return a >>= f ? f a
   */

  @Test
  fun testFlatMapLeftIdentity() {
    val a = 23
    val m = FRParseResult.succeed(listOf(), a)
    val f = { y: Int -> FRParseResult.FRParseSucceeded(listOf(), y * 2) }
    Assertions.assertEquals(m.flatMap(f), f(a))
  }

  /**
   * m >>= return ? m
   */

  @Test
  fun testFlatMapRightIdentity() {
    val m = FRParseResult.succeed(listOf(), 23)
    val r = m.flatMap { y -> succeed(listOf(), y) }
    Assertions.assertEquals(m, r)
  }

  /**
   * (m >>= f) >>= g ? m >>= (\x -> f x >>= g)
   */

  @Test
  fun testFlatMapAssociative() {
    val m = FRParseResult.succeed(listOf(), 23)
    val f = { y: Int -> succeed(listOf(), y * 2) }
    val g = { y: Int -> succeed(listOf(), y * 3) }

    Assertions.assertEquals(
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
    val m = FRParseResult.succeed(listOf(), a).map(f)
    val n = FRParseResult.succeed(listOf(), f(a))
    Assertions.assertEquals(m, n)
  }
}
