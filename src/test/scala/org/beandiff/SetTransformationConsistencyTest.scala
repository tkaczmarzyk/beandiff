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
import org.beandiff.test.JSet
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


@RunWith(classOf[JUnitRunner])
class SetTransfromationConsistencyTest extends FunSuite with ShouldMatchers {
  
  test("should be able to transform a set to any of its subsets") {
    val elems = List("a", "b", "c", "d", "e")
    val s1 = JSet(elems: _*)

    for {
      n <- 0 to elems.size
      subSet <- elems.combinations(n)
    } {
      val s2 = JSet(subSet: _*)

      diff(s1, s2).transformTarget()

      assert(s1 === s2, "Error for transformation: " + s2)
    }
  }

  test("should be able to transform a set to any of its subsets with additions") { // long execution time
    val elems = List("a", "b", "c", "d", "e")
    val other = List("x", "y", "z", "$", "#")

    val s1 = JSet(elems: _*)

    for {
      n1 <- 0 to elems.size
      subSet <- elems.combinations(n1)
      n2 <- 0 to other.size
      addons <- other.combinations(n2)
      mixedElems = (subSet ++ addons)
    } {
      val s2 = JSet(mixedElems: _*)

      diff(s1, s2).transformTarget()

      assert(s1 === s2, "Error for transformation: " + s2)
    }
  }
}