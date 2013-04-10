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
package org.beandiff.core

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.beandiff.core.model.Path


@RunWith(classOf[JUnitRunner])
class PathFilterTest extends FunSuite with ShouldMatchers {

  val filter = new PathFilter(Set(Path("aaa"), Path("bbb.ccc")))
  
  
  test("should skip if contains the path") {
    assert(filter.shouldSkip(Path("aaa"), null, null))
    assert(filter.shouldSkip(Path("bbb.ccc"), null, null))
  }
  
  test("should not skip if doesn't contain the path") {
    assert(!filter.shouldSkip(Path("ddd"), null, null))
    assert(!filter.shouldSkip(Path("bbb.eee"), null, null))
  }
}