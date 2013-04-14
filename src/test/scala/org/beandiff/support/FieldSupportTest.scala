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
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.beans.Simpsons
import org.beandiff.support.FieldSupport.RichField


@RunWith(classOf[JUnitRunner])
class FieldSupportTest extends FunSuite with ShouldMatchers with Simpsons {
  
  val nameField = classOf[SimpleJavaBean].getDeclaredField("name")
  nameField.setAccessible(true)
  
  
  test("should return None if object doesn't have such field") {
    nameField.getFrom(new Object) should be === None
  }
  
  test("should return value of the field") {
    nameField.getFrom(bart) should be === Some("bart")
  }
}