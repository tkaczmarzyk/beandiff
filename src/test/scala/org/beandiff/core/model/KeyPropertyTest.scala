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

import org.beandiff.test.JMap
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


@RunWith(classOf[JUnitRunner])
class KeyPropertyTest extends FunSuite with ShouldMatchers {

  val map = JMap("a" -> null, "b" -> 1)
  
  
  test("should return None if no such key") {
    KeyProperty("c").get(map) should be === None
  }
  
  test("should return Some if key exists") {
    KeyProperty("b").get(map) should be === Some(1)
    KeyProperty("a").get(map) should be === Some(null)
  }
  
  test("should put value under the key") {
    KeyProperty("c").setValue(map, 3)
    
    map.get("c") should be === 3
  }
 
  test("should overwrite old value") {
    KeyProperty("b").setValue(map, 2)
    
    map.get("b") should be === 2
  }
}