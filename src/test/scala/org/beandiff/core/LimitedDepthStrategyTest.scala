/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
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

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Path

@RunWith(classOf[JUnitRunner])
class LimitedDepthStrategyTest extends FunSuite with ShouldMatchers {

  private val o1 = new Object
  private val o2 = new Object
  
  private val limit3 = new LimitedDepthStrategy(3)
  
  
  test("should throw exception if depth = zero") {
    intercept[IllegalArgumentException] {
      new LimitedDepthStrategy(0)
    }
  }
  
  test("should throw exception if depth negative") {
    intercept[IllegalArgumentException] {
      new LimitedDepthStrategy(-1)
    }
  }
  
  test("should return false if path.depth after step is = max") {
    // TODO might be counter-intuitive: 
    // when the strategy prevents proceeding,
    // then equality is checked and a NewValue might be added which effectively increases the depth by 1
    limit3.shouldProceed(Path("aaa.bbb.ccc"), o1, o2) should be === false
  }
  
  test("should return false if depth greater than limit") { // should not happen normally
    limit3.shouldProceed(Path("aaa.bbb.ccc.ddd"), o1, o2) should be === false
  }
  
  test("should return true if max depth not reached yet") {
    limit3.shouldProceed(Path("aaa"), o1, o2) should be === true
    limit3.shouldProceed(Path("aaa.bbb"), o1, o2) should be === true
  }
}