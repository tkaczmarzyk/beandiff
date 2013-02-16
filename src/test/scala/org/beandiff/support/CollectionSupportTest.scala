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
package org.beandiff.support

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.beandiff.support.CollectionSupport.convert

@RunWith(classOf[JUnitRunner])
class CollectionSupportTest extends FunSuite with ShouldMatchers {

  private val abcd = List("a", "b", "c", "d")
  private val abcdefgh = abcd ::: List("e", "f", "g", "h")
  
  test("should drop all the elements") {
    abcd.dropIndices(List(0, 1, 2, 3)) should have size 0
  }
  
  test("should drop the first element") {
    abcd.dropIndices(List(0)) should be === List(("b", 1), ("c", 2), ("d", 3))
  }
  
  test("should just zip with index if index to be dropped is outside range") {
    abcd.dropIndices(List(4)) should be === abcd.zipWithIndex
  }
  
  test("should just zip with index if index to be dropped is negative") {
    abcd.dropIndices(List(-1)) should be === abcd.zipWithIndex
  }
  
  test("should drop single element from the middle") {
    abcd.dropIndices(List(2)) should be === List(("a", 0), ("b", 1), ("d", 3))
  }
  
  test("should skip invalid indices, but drop the correct ones") {
    abcd.dropIndices(List(-2, -1, 0, 1, 2, 4, 5)) should be === List(("d", 3))
  }
  
  test("should drop a sequence of elements") {
    abcdefgh.dropIndices(List(2, 3, 4, 5)) should be === List(("a", 0), ("b", 1), ("g", 6), ("h", 7))
  }
  
  test("should drop multiple sequences of elements") {
    abcdefgh.dropIndices(List(1, 2, 3, 5, 6)) should be === List(("a", 0), ("e", 4), ("h", 7))
  }
}