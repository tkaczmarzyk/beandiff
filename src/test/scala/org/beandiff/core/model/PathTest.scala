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

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import Path.EmptyPath
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class PathTest extends FunSuite with ShouldMatchers {

  test("should parse index property followed by field property") {
    assert(Path.of("[1].name") === new PathImpl(Vector(new IndexProperty(1), new FieldProperty("name"))))
  }
  
  test("should parse single index property") {
    assert(Path.of("[19]") === new PathImpl(new IndexProperty(19)))
  }
  
  test("should parse a sequence of indexes") {
    assert(Path.of("a[1][0]") === new PathImpl(Vector(new FieldProperty("a"), new IndexProperty(1), new IndexProperty(0))))
  }
  
  test("should parse index property") {
    assert(Path.of("a[1]") === new PathImpl(Vector(new FieldProperty("a"), new IndexProperty(1))))
  }
  
  test("should parse chain of property names") {
    assert(Path.of("a.b") === new PathImpl(Vector(new FieldProperty("a"), new FieldProperty("b"))))
  }
  
  test("toString should return parseable representation") {
    assert(Path.of("values[0].id").toString === "values[0].id")
  }
  
  test("empty + empty = empty") {
    (EmptyPath ++ EmptyPath) should be === EmptyPath
  }
  
  test("path step Self should yield path itself") {
    val path = Path(Property("name"))
    
    path.step(Self) should be === path
  }
  
  test("list.value[0] should be prefix of list.value[0].child.name") {
    Path.of("list.value[0]").isPrefixOf(Path.of("list.value[0].child.name")) should be === true
  }
  
  test("path should be prefix of itself") {
    Path.of("value.name").isPrefixOf(Path.of("value.name")) should be === true
  }
  
  test("aa.bb.cc is not a prefix of aa.bb") {
    Path.of("aa.bb.cc").isPrefixOf(Path.of("aa.bb")) should be === false
  }
}