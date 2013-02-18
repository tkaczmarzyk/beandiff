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
    presenter.present(diff1) should be === "values[0].id -- '17' vs '8'\n" + "name -- 'Aaa' vs 'Bbb'\n"
  }
  
  test("should present operations on list") {
    val diff = Diff(null, Self -> Diff(null, new Insertion("a", 0)),
        Property("[0]") -> Diff(null, new NewValue(Property("name"), "aa", "bb")))
        
    presenter.present(diff) should be === "[0].name -- 'aa' vs 'bb'\n" + ". -- inserted 'a' at [0]\n"
  }
}