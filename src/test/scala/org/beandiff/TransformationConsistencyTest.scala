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
package org.beandiff

import org.beandiff.BeanDiff.diff
import org.beandiff.beans.scala.Parent
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class TransformationConsistencyTest extends FunSuite with TransformationCheckUtils {
  
  test("BeanDiff.diff.transformTarget should transform a collection of Ints into other collection of Ints") {
    check((a: List[Int], b: List[Int]) => transformsJCollections(a, b))
  }

  test("BeanDiff.diff.transformTarget should transform a collection of Strings into other collection of Strings") {
    check((a: List[String], b: List[String]) => transformsJCollections(a, b))
  }
  
  test("BeanDiff.diff.transformTarget should transform a collection of Ints into a collection of Strings") {
    check((a: List[Int], b: List[String]) => transformsJCollections(a, b))
  }
  
  test("BeanDiff.diff.transformTarget should transform a collection of Parent beans into other collection of Parent beans") {
    check((a: List[Parent], b: List[Parent]) => transformsJCollections(a, b))
  }
}