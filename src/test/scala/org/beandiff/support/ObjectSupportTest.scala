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

import java.lang.Number
import java.lang.Object

import org.beandiff.support.ObjectSupport.convert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ObjectSupportTest extends FunSuite with ShouldMatchers {

  test("String.allClasses == (String, Object) ") {
    "aaa".allClasses should have size 2
    assert("aaa".allClasses.contains(classOf[String]))
    assert("aaa".allClasses.contains(classOf[Object]))
  }
  
  test("Integer.allClasses == (Integer, Number, Object)") {
    val classes = 1.allClasses
    classes should have size 3
    assert(classes.contains(classOf[Integer]))
    assert(classes.contains(classOf[Number]))
    assert(classes.contains(classOf[Object]))
  }
}