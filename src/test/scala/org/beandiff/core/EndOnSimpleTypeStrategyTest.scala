/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.beandiff.core

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import java.lang.Long
import java.lang.Double
import java.lang.Boolean
import java.lang.Byte
import org.beandiff.beans.SimpleEnum._
import java.lang.Float
import org.scalatest.junit.JUnitRunner
import org.beandiff.core.model.Path.EmptyPath


@RunWith(classOf[JUnitRunner])
class EndOnSimpleTypeStrategyTest extends FunSuite with ShouldMatchers {

  test("should end on String") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, "aa", "bb") should be (false)
  }
  
  test("should end on java Integer") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Integer.valueOf(1), Integer.valueOf(2)) should be (false)
  }
  
  test("should end on java Long") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Long.valueOf(1), Long.valueOf(2)) should be (false)
  }
  
  test("should end on java Float") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Float.valueOf(1.0f), Float.valueOf(2.0f)) should be (false)
  }
  
  test("should end on java Double") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Double.valueOf(1.0), Double.valueOf(2.0)) should be (false)
  }
  
  test("should end on java Boolean") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Boolean.TRUE, Boolean.TRUE) should be (false)
  }
  
  test("should end on java Character") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Character.valueOf('t'), Character.valueOf('k')) should be (false)
  }
  
  test("should end on java Enum") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, ONE, TWO) should be (false)
  }
  
  test("should end on java Byte") {
    EndOnSimpleTypeStrategy.shouldProceed(EmptyPath, Byte.MIN_VALUE, Byte.MAX_VALUE) should be (false)
  }
}