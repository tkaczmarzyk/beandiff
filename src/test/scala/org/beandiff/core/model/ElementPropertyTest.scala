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
package org.beandiff.core.model

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.beandiff.test.JSet
import org.beandiff.support.ClassDictionary


@RunWith(classOf[JUnitRunner])
class ElementPropertyTest extends FunSuite with ShouldMatchers {

  val elem = new Object
  
  test("should return the element") {
    ElementProperty(elem).get(JSet(elem)) should be === Some(elem)
  }
  
  test("should return None if target is not a set") {
    ElementProperty(elem).get(JSet(1, 2, 3)) should be === None
  }
  
  test("should return None if target doesn't contain the element") {
    ElementProperty(elem).get("abc") should be === None
  }
  
  test("should yield empty brackets if no toString for elem provided") {
    ElementProperty(elem).mkString should be === "[]"
  }
  
  test("should present the element using provided toString") {
    val dict = new ClassDictionary((o: Any) => "elem")
    ElementProperty(elem).mkString(dict) should be === "[elem]"
  }
}