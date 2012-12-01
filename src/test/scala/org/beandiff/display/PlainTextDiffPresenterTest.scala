/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.beandiff.core.model.Diff
import org.beandiff.core.model.LeafDiff
import org.beandiff.core.model.Path
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.ValueBean
import org.beandiff.beans.IdBean

@RunWith(classOf[JUnitRunner])
class PlainTextDiffPresenterTest extends FunSuite with ShouldMatchers {

  val presenter = new PlainTextDiffPresenter
  
  val bean1 = new ValueBean[IdBean]("Aaa", new IdBean(17))
  val bean2 = new ValueBean[IdBean]("Bbb", new IdBean(8))
  
  val diff1 = new Diff(bean1, bean2) // TODO reduce verbosity and dependency to other functionality
  diff1(Path.of("name")) = new LeafDiff(bean1.name, bean2.name)
  // TODO ? consider using solution with stepBack to autopopulate intermediate paths {{
  diff1(Path.of("values")) = new Diff(bean1.values, bean2.values)
  diff1(Path.of("values[0]")) = new Diff(bean1.values.get(0), bean2.values.get(0))
  //}}
  diff1(Path.of("values[0].id")) = new LeafDiff(bean1.values.get(0).id, bean2.values.get(0).id)
  
  
  test("should display simple properties") {
    presenter.present(diff1) should be === "name -- 'Aaa' vs 'Bbb'\n" + "values[0].id -- '17' vs '8'\n"
  }
}