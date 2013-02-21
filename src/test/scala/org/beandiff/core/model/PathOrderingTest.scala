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

import org.beandiff.BeanDiff
import org.beandiff.beans.ParentBean
import org.beandiff.test.BeanDiffMatchers._
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.NewValue
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito.mock
import org.beandiff.core.model.change.NewValue
import org.beandiff.TestDefs._
import org.scalatest.FunSuite
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class PathOrderingTest extends FunSuite with ShouldMatchers {

  private val ordering = PathOrdering
  
  test("shorter should be first") {
    ordering.compare(Path("child[0].value"), Path("child[0]")) should be > 0
    ordering.compare(Path("child[0]"), Path("child[0].value")) should be < 0
  }
  
  test("should order lexicographically") {
    ordering.compare(Path("name"), Path("aaa")) should be > 0
    ordering.compare(Path("aaa"), Path("name")) should be < 0
  }
}