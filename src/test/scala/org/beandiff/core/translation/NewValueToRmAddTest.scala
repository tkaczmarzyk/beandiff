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
package org.beandiff.core.translation

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.beandiff.TestDefs._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.Property
import org.beandiff.core.model.change.Removal
import org.beandiff.core.model.change.Addition


@RunWith(classOf[JUnitRunner])
class NewValueToRmAddTest extends FunSuite with ShouldMatchers {

  private val translation = new NewValueToRmAdd()
  
  
  test("should throw exception if the change is not a NewValue") {
    intercept[IllegalArgumentException] {
      translation.translate(mock[Change])
    }
  }
  
  test("should throw exception if new-value's property is not an index") {
    intercept[IllegalArgumentException] {
      translation.translate(NewValue(Property("name"), new Object, new Object))
    }
  }
  
  test("should translate to Removal + Addition") {
    val o1 = new Object
    val o2 = new Object
    
    val translated = translation.translate(NewValue(Property("[0]"), o1, o2))
    translated.toList should be === List(Removal(o1), Addition(o2))
  }
}