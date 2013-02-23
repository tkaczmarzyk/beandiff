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

import org.junit.runner.RunWith
import org.beandiff.TestDefs.mockChange
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Property

@RunWith(classOf[JUnitRunner])
class ChangeOrderingTest extends FunSuite with ShouldMatchers {

  private val o = new Object
  
  test("deletion with higher index should be first") {
    ChangeOrdering.compare(new Deletion(o, 1), new Deletion(o, 2)) should be > 0
  }
  
  test("deletion should be before an insertion regardless their indices") {
    ChangeOrdering.compare(new Deletion(o, 1), new Insertion(o, 2)) should be < 0
    ChangeOrdering.compare(new Deletion(o, 2), new Insertion(o, 1)) should be < 0
    ChangeOrdering.compare(new Deletion(o, 0), new Insertion(o, 0)) should be < 0
    
    ChangeOrdering.compare(new Insertion(o, 2), new Deletion(o, 1)) should be > 0
    ChangeOrdering.compare(new Insertion(o, 1), new Deletion(o, 2)) should be > 0
    ChangeOrdering.compare(new Insertion(o, 0), new Deletion(o, 0)) should be > 0
  }
  
  test("new-value should be before any other change") {
    val newValue = new NewValue(Property("test"), o, o)
    ChangeOrdering.compare(newValue, mockChange()) should be < 0
    ChangeOrdering.compare(mockChange(), newValue) should be > 0
  }
  
  test("insertion with lower index should be first") {
    val i1 = new Insertion(o, 1)
    val i2 = new Insertion(o, 2)
    ChangeOrdering.compare(i1, i2) should be < 0
    ChangeOrdering.compare(i2, i1) should be > 0
  }
  
  test("new-values should be ordered by property") {
    ChangeOrdering.compare(new NewValue(Property("aaa"), 1, 2), new NewValue(Property("bbb"), 1, 2)) should be < 0
  }
  
  test("removal should be before addition") {
    val rm = new Removal(new Object)
    val add = new Addition(new Object)
    ChangeOrdering.compare(rm, add) should be < 0
    ChangeOrdering.compare(add, rm) should be > 0
  }
}