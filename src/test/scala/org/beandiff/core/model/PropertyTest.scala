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

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.test.JList
import org.beandiff.beans.SimpleJavaBean

@RunWith(classOf[JUnitRunner])
class PropertyTest extends FunSuite with ShouldMatchers {

  private val o = new Object
  
  test("should return None if no such property") {
    Property("name").get(o) should be === None
    Property("[0]").get(o) should be === None
//    Property("[key]").get(o) should be === None
  }
  
  test("should yield Some with the object") {
    Self.get(o) should be === Some(o)
  }
  
  test("should return Some with correct val if index property exist") {
    val l = JList("a", "b", "c")
    Property("[0]").get(l) should be === Some("a")
    Property("[1]").get(l) should be === Some("b")
    Property("[2]").get(l) should be === Some("c")
  }
  
  test("should return Some with correct val if field property exist") {
    val o = new SimpleJavaBean("aaa", 12)
    Property("name").get(o) should be === Some("aaa")
    Property("value").get(o) should be === Some(12)
  }
}