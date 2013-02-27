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
package org.beandiff

import org.beandiff.DiffEngineBuilder.aDiffEngine
import org.beandiff.DiffEngineBuilder.builder2engine
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.beans.Simpsons
import org.beandiff.core.model.Diff
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.Property
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.NewValue
import org.beandiff.test.BeanDiffMatchers.haveDifference
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.test.JSet
import org.beandiff.core.model.change.Removal
import org.beandiff.core.model.change.Addition


@RunWith(classOf[JUnitRunner])
class DiffEngineBuilderTest extends FunSuite with ShouldMatchers with Simpsons {

  test("should be case sensitive when not configured otherwise") {
    aDiffEngine.calculateDiff("AAA", "aaa") should haveDifference
  }
  
  test("should ignore case when configured") {
    val engine = aDiffEngine.ignoringCase.build()
    engine.calculateDiff("AAA", "aaa") should not (haveDifference)
  }
  
  test("should treat everything as value type if not configured otherwise") {
    aDiffEngine.calculateDiff(maggie, lisa) should be === Diff(maggie,
        NewValue(Property("value"), 1, 8), NewValue(Property("name"), "maggie", "lisa"))
  }
  
  test("should treat list element as value if not specified otherwise") {
    aDiffEngine.calculateDiff(JList(maggie), JList(lisa)) should be === Diff(JList(maggie),
        IndexProperty(0) -> Diff(maggie, NewValue(Property("value"), 1, 8), NewValue(Property("name"), "maggie", "lisa")))
  }
  
  test("should treat list element of the specified class as entity") {
    val engine = aDiffEngine.withEntity[SimpleJavaBean]("name")
    engine.calculateDiff(JList(maggie), JList(lisa)) should be === Diff(JList(maggie),
        NewValue(IndexProperty(0), maggie, lisa))
  }
  
  test("should treat set element of the specified class as entity") {
    val engine = aDiffEngine.withEntity[SimpleJavaBean]("name")
    engine.calculateDiff(JSet(maggie), JSet(lisa)) should be === Diff(JSet(maggie),
        Addition(lisa), Removal(maggie))
  }
  
  // TODO
  ignore("should treat the specified class as entity") {
    val engine = aDiffEngine.withEntity[SimpleJavaBean]("name")
    engine.calculateDiff(maggie, lisa) should be === Diff(maggie, NewValue(Self, maggie, lisa))
  }
  
}