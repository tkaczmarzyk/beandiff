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
package org.beandiff.lcs

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.TestDefs.EverythingIsSimpleVal


@RunWith(classOf[JUnitRunner])
class LcsCalcTest extends FunSuite with ShouldMatchers {

  private val calc = new NaiveLcsCalc
  

  test("lcs of empty lists should be empty") {
    calc.lcs(List(), List())(EverythingIsSimpleVal) should be === List()
  }
  
  test("lcs of empty and non-empty should be empty") {
    calc.lcs(List(1, 2, 3), List())(EverythingIsSimpleVal) should be === List()
  }
  
  test("lcs of a seq and its copy should be the whole seq") {
    calc.lcs("abc", "abc")(EverythingIsSimpleVal) should be === List(Occurence('a', 0, 0), Occurence('b', 1, 1), Occurence('c', 2, 2))
  }
  
  test("lcs(human, chimpanzee) should be hman") {
    calc.lcs("human", "chimpanzee")(EverythingIsSimpleVal) should be ===
      List(Occurence('h', 0, 1), Occurence('m', 2, 3), Occurence('a', 3, 5), Occurence('n', 4, 6))
  }
}