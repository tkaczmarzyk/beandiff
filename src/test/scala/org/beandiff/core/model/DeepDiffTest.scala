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
import org.mockito.Mockito.inOrder
import org.beandiff.test.BeanDiffMatchers._
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.NewValue
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.beandiff.TestDefs._
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.NewValue
import org.mockito.InOrder

@RunWith(classOf[JUnitRunner])
class DeepDiffTest extends FunSuite with ShouldMatchers { // TODO eliminate hasDifference in assertions

  val obj = new Object()
  val child = new SimpleJavaBean("aa", 1)
  val parent = new ParentBean("parent", child)
  val grandpa = new ParentBean("grandpa", parent)
  val simpleDiff = Diff(child, new NewValue(Property("name"), "aa", "bb"))
  
  
  test("should add all property changes at the path") { // TODO test for withChanges(property, ...)
    val parentDiff = new DeepDiff(parent, Map(Property("name") -> mockDiff()))
    val childDiff = new DeepDiff(child, Map(Property("name") -> mockDiff()))
    
    val merged = parentDiff.withChanges(Path("child"), childDiff)
    merged should haveDifference("child.name")
  }
  
  test("there should be no difference in simple property after transformation") {
    simpleDiff.transformTarget()
    child.getName() should be === "bb"
  }
  
  test("should return a single leaf change") {
    val change = mock[NewValue]
    val diff = new DeepDiff(null, Map(new IndexProperty(0) -> new FlatDiff(1, change)))
    
    diff.leafChanges should be === List((Path("[0]"), change))
  }
  
  test("should remove subdiff on the provided path") {
    val grandChildNameDiff = Diff(child, new NewValue(Property("name"), "bb", "cc"))
    val diff = Diff(grandpa, Map(Property("child") -> Diff(parent,
        Map(Property("name") -> mockDiff(), Property("child") -> grandChildNameDiff))))
    
    diff.without(Path("child.name")) should be === Diff(grandpa, Map(Property("child") -> Diff(parent,
        Map(Property("child") -> grandChildNameDiff))))
  }
  
  test("should remove a diff that becomes empty after removal of a last subdiff") {
    val grandChildNameDiff = Diff(child, new NewValue(Property("name"), "bb", "cc"))
    val diff = Diff(grandpa, Property("child") -> Diff(parent, Property("child") -> grandChildNameDiff))
    
    diff.without(Path("child.child")) should not (haveDifference)
  }
  
  test("should remove a diff that becomes empty after removal of a last change") {
    val grandChildNameDiff = Diff(child, new NewValue(Property("name"), "bb", "cc"))
    val diff = Diff(grandpa, Property("child") -> Diff(parent, Property("child") -> grandChildNameDiff))
    
    diff.without(Path("child.child.name")) should not (haveDifference)
  }
  
  test("should add changes at the specified path") {
    val diff = Diff(grandpa)
    val added = Diff(child, new NewValue(Property("name"), "bb", "cc"))
    val modified = diff.withChanges(Path("child.child"), added)
    
    modified should be === Diff(grandpa, Property("child") -> Diff(parent, Property("child") -> added))
  }
  
  test("should yield the nested diff") {
    val nestedChanges = mock[Diff]
    val diff = new DeepDiff(parent,
        Map(Property("child") -> new DeepDiff(child, 
            Map(Property("name") -> nestedChanges)))) // TODO simplify the creation (builder?)
    
    diff.changes(Path("child.name")).get should be === nestedChanges
  }
  
  test("should add change at the path and create all intermediate diff object") {
    val newChange = mockChange()
    val diff = Diff(grandpa)
    
    diff.withChange(Path("child.child"), newChange) should be === Diff(grandpa, Property("child") -> Diff(parent, Property("child") -> Diff(child, newChange)))
  }
  
  test("should add change to the existing subdiff") {
    val oldChange, newChange = mockChange()
    val diff = Diff(parent, Property("child") -> Diff(child, oldChange))
    
    diff.withChange(Path("child"), newChange) should be === Diff(parent, Property("child") -> Diff(child, newChange, oldChange))
  }
  
  test("should merge the existing subdiff with a new one") {
    val change1 = mockChange("ch1")
    val change2 = mockChange("ch2")
    val subdiff = Diff(child, change1)
    val diff = Diff(parent, Map(Property("child") -> subdiff))
    val newSubdiff = Diff(child, change2)
    
    diff.withChanges(Property("child"), newSubdiff) should be === Diff(parent, Property("child") -> Diff(child, change2, change1))
  }
  
  test("should remove a subdiff at the path") {
    val change = mockChange()
    val diff = Diff(parent, Property("child") -> Diff(child, change), Property("name") -> mockDiff())
    
    diff.without(Path("name")) should be === Diff(parent, Property("child") -> Diff(child, change))
  }
  
  test("should become an empty diff if removing the last change") {
    val diff = Diff(parent, Property("name") -> mockDiff())
    
    diff.without(Path("name")) should be === Diff(parent)
  }
  
  test("should remove the nested diff when it becomes empty") {
    val change = mockChange()
    val diff = Diff(parent, Map(Property("child") -> Diff(child, change)))
    
    diff.without(Path("child"), change) should be === Diff(parent)
  }
  
  test("should remove the whole branch of subdiffs when the last leaf change is removed") {
    val change = mockChange()
    val diff = Diff(obj, Property("aaa") -> Diff(null, Property("bbb") -> Diff(null, Property("ccc") -> Diff(null, change))))
    
    diff.without(Path("aaa.bbb.ccc"), change) should be === Diff(obj)
  }
  
  test("should not add anything if the subdiff to be added is empty") {
    val diff = Diff(parent, Property("name") -> mockDiff())
    
    diff.withChanges(Path("child"), Diff(child)) should be === diff
  }
  
  test("should return empty collection if no changes at the path") {
    val d = Diff(obj, Property("testProp") -> mockDiff())
    
    d.changes(EmptyPath) should be ('empty)
  }
  
  test("should yield itself if doesnt have a self-change to be removed") {
    val d = Diff(obj, Property("testProp") -> mockDiff())
    
    d.without(EmptyPath, mockChange()) should be === d
  }
  
  test("should yield itself if doesnt have a change to be removed") {
    val d = Diff(obj, Property("testProp") -> mockDiff())
    
    d.without(Path("unexisting"), mockChange()) should be === d
  }
  
  test("should indicate difference if has a change with the target property") {
    val diff = new DeepDiff(parent, Map(Self -> new FlatDiff(parent, new NewValue(Property("name"), "parent", "newName"))))
    
    diff should haveDifference(Path("name"))
  }
  
  test("should be able to change the target") {
    val diff = new DeepDiff(parent, Map(Property("child") -> Diff(child, NewValue(Property("name"), "aa", "bb"))))
    
    diff.forTarget(grandpa).target should be === grandpa
  }
  
  test("should change target in nested self-diff") {
    val diff = new DeepDiff(parent, Map(Self -> Diff(parent, NewValue(Property("name"), "parent", "newName")),
        Property("child") -> Diff(child, NewValue(Property("name"), "aa", "bb"))))
    
    diff.forTarget(grandpa).changes(EmptyPath).get.target should be === grandpa
  }
  
  test("should yield itself when list of changes is empty") {
    val diff = new DeepDiff(parent, Map(Property("name") -> mockDiff()))
    
    diff.withChanges(Path("child"), List()) should be === diff
  }
  
  test("should perform deeper changes first") {
    val selfCh = mockChange("selfCh")
    val deepCh = mockChange("deepCh")
    val d = Diff(parent, Property("child") -> Diff(child, deepCh), Self -> Diff(parent, selfCh))
    
    d.transformTarget()
    
    val order = inOrder(selfCh, deepCh)
    order.verify(deepCh).perform(child)
    order.verify(selfCh).perform(parent)
  }
}