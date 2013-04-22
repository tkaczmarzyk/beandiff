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

import org.beandiff.TestDefs.mock
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Removal


@RunWith(classOf[JUnitRunner])
class DeletionToRemovalTest extends FunSuite with ShouldMatchers {

  val translation = new DeletionToRemoval
  
  test("should throw an exception if not a Removal") {
    intercept[IllegalArgumentException] {
      translation.translate(mock[Change])
    }
  }
  
  test("should translate to Removal with the oldValue") {
    val elem = new Object
    val del = new Deletion(elem, 2)
    
    translation.translate(del) should be === List(new Removal(elem))
  }
}