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
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.NewValue
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito.mock
import org.beandiff.core.model.change.NewValue

@RunWith(classOf[JUnitRunner])
class DeepDiffTest extends FunSuite with ShouldMatchers {

  val child = new SimpleJavaBean("aa", 1)
  val parent = new ParentBean("parent", child)
  val simpleDiff = BeanDiff.diff(child, new SimpleJavaBean("bb", 1)) // FIXME do not rely on BeanDiff.diff
  
  val childNameDiff = new DeepDiff(parent,
        Map(new FieldProperty("child") -> new DeepDiff(child, 
            Map(new FieldProperty("name") -> new FlatDiff(null, new NewValue(child, new FieldProperty("name"), "bb", "cc")))))) // FIXME null // TODO simplify the creation (builder?)
  
  
  test("should add change at the path") {
    val diff = new DeepDiff(parent, Map())
    val updated = diff.withChange(Path.of("child.name"), new NewValue(child, new FieldProperty("name"), ":(", ":)"))
    
    val lea = updated.leafChanges
    assert(updated.leafChanges.exists(_._1 == Path.of("child.name")))
  }
  
  test("there should be no difference in simple property after transformation") {
    simpleDiff.transformTarget()
    child.getName() should be === "bb"
  }
  
  test("should return a single leaf change") {
    val change = mock(classOf[NewValue])
    val diff = new DeepDiff(null, Map(new IndexProperty(0) -> new FlatDiff(1, change)))
    
    diff.leafChanges should be === List((Path("[0]"), change))
  }
  
  test("should remove subdiff on the provided path") {
    childNameDiff.without(Path("child.name")) should not (haveDifference("child.name"))
  }
  
  test("should remove a diff that becomes empty after removal") {
    childNameDiff.without(Path("child.name")) should not (haveDifference)
  }
  
  test("should add changes at the specified path") {
    val diff = Diff(parent)
    val added = new FlatDiff(null, new NewValue(child, new FieldProperty("name"), "bb", "cc"))
    val modified = diff.withChanges(Path("child.name"), added)
    
    modified should haveDifference("child.name") // TODO better assertion
  }
  
  test("should yield the nested changeset") {
    val nestedChanges = mock(classOf[Diff])
    val diff = new DeepDiff(parent,
        Map(new FieldProperty("child") -> new DeepDiff(child, 
            Map(new FieldProperty("name") -> nestedChanges)))) // TODO simplify the creation (builder?)
    
    diff.changes(Path("child.name")) should be === nestedChanges
  }
}