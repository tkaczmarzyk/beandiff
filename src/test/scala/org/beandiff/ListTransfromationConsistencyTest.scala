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
import org.beandiff.DiffEngineBuilder._
import org.beandiff.TestDefs.EverythingIsEntityWithNameId
import org.beandiff.test.ObjectTestSupport.convert
import org.beandiff.TypeDefs._
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Path
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.beandiff.beans.SimpleJavaBean._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.CollectionBean
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.LcsDiffEngine
import org.beandiff.core.DiffEngineCoordinator
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.beans.Simpsons
import java.util.ArrayList
import java.util.Arrays
import org.scalatest.FunSuite


@RunWith(classOf[JUnitRunner])
class ListTransfromationConsistencyTest extends FunSuite with ShouldMatchers {
  
  test("should be able to transform a list to any permutation of its sublists") {
    val list = List("a", "b", "c", "d", "e")
    val l1 = JList(list: _*)

    for {
      n <- 0 to list.size
      subList <- list.combinations(n)
      subPerm <- subList.permutations
    } {
      val l2 = JList(subPerm: _*)

      diff(l1, l2).transformTarget()

      assert(l1 === l2, "Error for transformation: " + l2)
    }
  }

  test("should be able to transform a list to any of its permutations with insertions") { // long execution time
    val list = List("a", "b", "c", "d")
    val other = List("x", "y", "z", "$")

    val l1 = JList(list: _*)

    for {
      n1 <- 0 to list.size
      subList <- list.combinations(n1)
      n2 <- 0 to other.size
      addons <- other.combinations(n2)
      subPerm <- (subList ++ addons).permutations
    } {
      val l2 = JList(subPerm: _*)

      diff(l1, l2).transformTarget()

      assert(l1 === l2, "Error for transformation: " + l2)
    }
  }
}