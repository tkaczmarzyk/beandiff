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
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.NewValue
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito.mock

@RunWith(classOf[JUnitRunner])
class DiffTest extends FunSuite with ShouldMatchers {

  val simpleBean1 = new SimpleJavaBean("aa", 1)
  val parent = new ParentBean("parent", simpleBean1)
  val simpleDiff = BeanDiff.diff(simpleBean1, new SimpleJavaBean("bb", 1))
  
  test("should add change at the path") {
    val diff = new DiffImpl(EmptyPath, parent, Map())
    val updated = diff.withChange(Path.of("child.name"), new NewValue(parent, new FieldProperty("name"), ":(", ":)"))
    
    assert(updated.leafChanges.exists(_._1 == Path.of("child.name")))
  }
  
  test("there should be no difference in simple property after transformation") {
    simpleDiff.transformTarget()
    simpleBean1.getName() should be === "bb"
  }
  
  test("should return a single leaf change") {
    val change = mock(classOf[NewValue])
    val diff = new DiffImpl(EmptyPath, JList(1), Map(new IndexProperty(0) -> ChangeSet(1, Path("[0]"), change)))
    
    diff.leafChanges should be === List((Path("[0]"), change))
  }
  
  ignore("should skip its own path when returning leafChanges?") {
    val change = mock(classOf[NewValue])
    val diff = new DiffImpl(Path("outerProp"), JList(1), Map(new IndexProperty(0) -> ChangeSet(1, Path("[0]"), change)))
    
    diff.leafChanges should be === List((Path("[0]"), change))
  }
}