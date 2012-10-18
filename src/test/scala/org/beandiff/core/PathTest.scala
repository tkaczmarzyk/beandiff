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
package org.beandiff.core

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PathTest extends FunSuite {

  test("should parse index property followed by field property") {
    assert(Path.of("[1].name") === new Path(new IndexProperty(1), new Path(new FieldProperty("name"))))
  }
  
  test("should parse single index property") {
    assert(Path.of("[19]") === new Path(new IndexProperty(19)))
  }
  
  test("should parse a sequence of indexes") {
    assert(Path.of("a[1][0]") === new Path(new FieldProperty("a"), new Path(new IndexProperty(1), new Path(new IndexProperty(0)))))
  }
  
  test("should parse index property") {
    assert(Path.of("a[1]") === new Path(new FieldProperty("a"), new Path(new IndexProperty(1))))
  }
  
  test("should parse chain of property names") {
    assert(Path.of("a.b") === new Path(new FieldProperty("a"), new Path(new FieldProperty("b"))))
  }
  
  test("toString should return parseable representation") {
    assert(Path.of("values[0].id").toString === "values[0].id")
  }
}