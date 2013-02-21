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
package org.beandiff.display

import org.beandiff.beans.IdBean
import org.beandiff.beans.ValueBean
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Property
import org.beandiff.core.model.FieldProperty
import org.beandiff.core.model.FlatDiff
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.change.NewValue
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue

@RunWith(classOf[JUnitRunner])
class PlainTextDiffPresenterTest extends FunSuite with ShouldMatchers {

  val presenter = new PlainTextDiffPresenter
  
  val bean1 = new ValueBean[IdBean]("Aaa", new IdBean(17))
  val bean2 = new ValueBean[IdBean]("Bbb", new IdBean(8))
  
  val valueDiff = Diff(bean1.values.get(0), new NewValue(Property("id"), 17, 8))
  val valuesDiff = Diff(bean1.values, new IndexProperty(0) -> valueDiff)
  val diff1 = Diff(bean1, 
      Self -> Diff(bean1, new NewValue(Property("name"), "Aaa", "Bbb")), Property("values") -> valuesDiff)
  
  
  test("should display simple properties") {
    presenter.present(diff1) should be === "name -- 'Aaa' vs 'Bbb'\n" + "values[0].id -- '17' vs '8'\n"
  }
  
  test("should present operations on list") {
    val diff = Diff(null, Self -> Diff(null, new Insertion("a", 0)),
        Property("[0]") -> Diff(null, new NewValue(Property("name"), "aa", "bb")))
        
    presenter.present(diff) should be === ". -- inserted 'a' at [0]\n" + "[0].name -- 'aa' vs 'bb'\n"
  }
  
  test("should order by insertion index, not by the value") {
    val diff = Diff(null, new Insertion("a", 1), new Insertion("b", 0))
    
    presenter.present(diff) should be === ". -- inserted 'b' at [0]\n" + ". -- inserted 'a' at [1]\n"
  }
  
  test("should present the full path of a nested change") {
    val diff = Diff(null, Property("children") -> Diff(null, Property("[0]") -> Diff(null, new NewValue(Property("name"), "a", "b"))))
  
    presenter.present(diff) should be === "children[0].name -- 'a' vs 'b'\n"
  }
  
  test("should order the changes lexicographically") {
    val diff = Diff(null, Self -> Diff(null, new Insertion("a", 1), new Insertion("b", 0), new NewValue(Property("name"), "old", "new")),
        Property("children") -> Diff(null, Property("[1]") -> Diff(null, new NewValue(Property("name"), "c", "d")),
            Property("[0]") -> Diff(null, new NewValue(Property("value"), 2, 3),
                new NewValue(Property("age"), 0, 1), new NewValue(Property("time"), 1, 2))))
            
    presenter.present(diff) should be === "name -- 'old' vs 'new'\n" + ". -- inserted 'b' at [0]\n" + ". -- inserted 'a' at [1]\n" +
        "children[0].age -- '0' vs '1'\n" + "children[0].time -- '1' vs '2'\n" + "children[0].value -- '2' vs '3'\n" + "children[1].name -- 'c' vs 'd'\n"
  }
}