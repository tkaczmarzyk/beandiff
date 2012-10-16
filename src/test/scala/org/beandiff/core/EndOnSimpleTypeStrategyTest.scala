package org.beandiff.core

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import java.lang.Long
import java.lang.Double
import java.lang.Boolean
import org.beandiff.beans.SimpleEnum._
import java.lang.Float
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class EndOnSimpleTypeStrategyTest extends FunSuite with ShouldMatchers {

  test("should end on String") {
    EndOnSimpleTypeStrategy.shouldProceed("aa", "bb") should be (false)
  }
  
  test("should end on java Integer") {
    EndOnSimpleTypeStrategy.shouldProceed(Integer.valueOf(1), Integer.valueOf(2)) should be (false)
  }
  
  test("should end on java Long") {
    EndOnSimpleTypeStrategy.shouldProceed(Long.valueOf(1), Long.valueOf(2)) should be (false)
  }
  
  test("should end on java Float") {
    EndOnSimpleTypeStrategy.shouldProceed(Float.valueOf(1.0f), Float.valueOf(2.0f)) should be (false)
  }
  
  test("should end on java Double") {
    EndOnSimpleTypeStrategy.shouldProceed(Double.valueOf(1.0), Double.valueOf(2.0)) should be (false)
  }
  
  test("should end on java Boolean") {
    EndOnSimpleTypeStrategy.shouldProceed(Boolean.TRUE, Boolean.TRUE) should be (false)
  }
  
  test("should end on java Character") {
    EndOnSimpleTypeStrategy.shouldProceed(Character.valueOf('t'), Character.valueOf('k')) should be (false)
  }
  
  test("should end on java Enum") {
    EndOnSimpleTypeStrategy.shouldProceed(ONE, TWO) should be (false)
  }
}