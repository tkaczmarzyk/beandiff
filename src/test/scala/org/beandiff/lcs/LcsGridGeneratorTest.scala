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
package org.beandiff.lcs

import org.beandiff.TestDefs.EverythingIsSimpleVal
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class LcsGridGeneratorTest extends FunSuite with ShouldMatchers {

  val gen = new LcsGridGenerator
  
  
  test("should create correct grid for 'art' and 'tar'") {
    val grid = gen.generate("art", "tar")(EverythingIsSimpleVal)
    
    grid should be === Map(
        (0, 0) -> (0, 'right), (0, 1) -> (1, 'diag), (0, 2) -> (1, 'right),
        (1, 0) -> (0, 'right), (1, 1) -> (1, 'down), (1, 2) -> (2, 'diag),
        (2, 0) -> (1, 'diag), (2, 1) -> (1, 'right), (2, 2) -> (2, 'down))
  }
}