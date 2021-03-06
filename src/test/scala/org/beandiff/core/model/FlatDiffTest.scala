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


@RunWith(classOf[JUnitRunner])
class FlatDiffTest extends FunSuite with ShouldMatchers {

  val target = new Object()
  val parent = new ParentBean("parent", target)
  
  test("empty FlatDiff should be converted to an empty DeepDiff") { // TODO abstraction leak...
    val converted = new FlatDiff(target).toDiff
    
    converted should not (haveDifference)
  }
  
  test("empty flat diff without changes should become the one it's being merged with") {
    val flat = new FlatDiff(parent)
    val deep = new DeepDiff(parent, Map(new FieldProperty("name") -> mockDiff()))
    
    flat.withChanges(Self, deep) should be === deep
  }
  
  test("empty flat diff should become the one it's being merged with (path arg)") { // FIXME almost duplicated test
    val flat = new FlatDiff(parent)
    val deep = new DeepDiff(parent, Map(new FieldProperty("name") -> mockDiff()))
    
    flat.withChanges(Path(Self), deep) should be === deep
  }
  
  test("a nested change should be added at the specified property") {
    val flat = new FlatDiff(parent)
    val subDiff = new FlatDiff(null, List(mockChange()))
    val merged = flat.withChanges(new FieldProperty("name"), subDiff)
    
    merged should be === new DeepDiff(parent, Map(new FieldProperty("name") -> subDiff))
  }
  
  test("should be still a flat diff when a self-change is added") { // TODO eliminate type checks
    val diff = new FlatDiff(parent).withChange(Self, mockChange())
    
    diff.getClass() should be === classOf[FlatDiff]
  }
  
  test("should yield an empty diff") {
    val diff = new FlatDiff(parent, mockChange())

    diff.without(Self) should be === new FlatDiff(parent)
  }
  
  test("should remove a change with the provided targetProperty") {
    val diff = new FlatDiff(parent, new NewValue(Property("name"), "parent", "newName"))
    
    diff.without(Property("name")) should be === new FlatDiff(parent)
  }
  
  test("should yield and empty diff (path arg)") { // FIXME test duplication
    val diff = new FlatDiff(parent, mockChange())
    
    diff.without(EmptyPath) should be === new FlatDiff(parent)
  }
  
  test("removing from a not-Self property should do nothing") {
    val diff = new FlatDiff(parent, mockChange())
    
    diff.without(Property("child")) should be === diff
  }
  
  test("should yield a diff for the new target but with the old changes") {
    val diff = new FlatDiff(parent, mockChange())
    val newDiff = diff.forTarget(target)
    
    newDiff.target should be === target
    newDiff.leafChanges should be === diff.leafChanges
  }
  
  test("should yield itself when taking from empty path")	{
    val diff = new FlatDiff(target, mockChange())
    
    diff.changes(EmptyPath).get should be === diff
  }
  
  test("should indicate difference if has a change with the target property") {
    val diff = new FlatDiff(parent, new NewValue(Property("name"), "parent", "newName"))
    
    assert(diff.hasDifference(Path("name")))
  }
  
  test("should yield itself when list of changes is empty") {
    val diff = new FlatDiff(target, mockChange())
    
    diff.withChanges(EmptyPath, List()) should be === diff
  }
  
  test("should correctly combine with DeepDiff") {
    val flat = new FlatDiff(parent, mockChange())
    val deep = Diff(target, Property("test") -> mockDiff())
    
    val merged = flat.withChanges(Path("child"), deep)
    merged should be === Diff(parent, Self -> flat, Property("child") -> deep)
  }
  
  test("should correctly combine with other flat diff") {
    val ch1 = mockChange("ch1")
    val ch2 = mockChange("ch2")
    
    val f1 = new FlatDiff(target, ch1)
    val f2 = new FlatDiff(target, ch2)
    
    f1.withChanges(EmptyPath, f2) should be === new FlatDiff(target, ch2, ch1)
  }
  
  test("should yield itself when other diff has no changes") {
    val d = Diff(parent, mockChange())
    val d2 = Diff(target)
    
    d.withChanges(Path("child"), d2) should be === d
  }
}