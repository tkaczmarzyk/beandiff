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
package org.beandiff.core.model.change

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.beandiff.beans.Simpsons
import org.beandiff.core.model.Property

@RunWith(classOf[JUnitRunner])
class NewValueTest extends FunSuite with ShouldMatchers with Simpsons {

  test("should update field value") {
    new Simpsons {
      val change = NewValue(Property("value"), 10, 30)
      change.perform(bart)
      bart.getValue should be === 30
    }
  }
  
  test("should do nothing if no new value") {
    val change = NewValue(Property("name"), Some(1), None)
    change.perform(bart)
    
    bart.getName should be === "bart"
  }
  
  test("should do nothing if no old value") {
    val change = NewValue(Property("xyz"), None, Some(2))
    change.perform(bart)
    
    bart.getName should be === "bart"
    bart.getValue should be === 10
  }
}