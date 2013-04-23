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

import org.beandiff.TestDefs.anyChange
import org.beandiff.TestDefs.anyDiff
import org.beandiff.TestDefs.anyProp
import org.beandiff.TestDefs.mock
import org.beandiff.TestDefs.mockChange
import org.beandiff.TestDefs.mockCoordinator
import org.beandiff.TestDefs.mockMap
import org.beandiff.TestDefs.of
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Property
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.Addition
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Removal
import org.beandiff.core.model.change.Shift
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.translation.DeletionToRemoval
import org.beandiff.core.translation.InsertionToAddition
import org.beandiff.core.translation.NewValueToRmAdd
import org.beandiff.core.translation.ShiftToNothing
import org.beandiff.test.BeanDiffMatchers.haveChange
import org.beandiff.test.BeanDiffMatchers.haveChanges
import org.beandiff.test.BeanDiffMatchers.haveDifference
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Self
import org.beandiff.core.model.FieldProperty


@RunWith(classOf[JUnitRunner])
class TransformingDiffEngineTest extends FunSuite with ShouldMatchers {
  
  type PropertyTranslation = (Property, Any) => Property
  
  val delegate = mock[DiffEngineCoordinator]
  val translation = mock[ChangeTranslation]
  val mockPropTranslation = mock[PropertyTranslation]
  val mockPropTranslations: Map[Class[_ <: Property], PropertyTranslation] = mockMap(mockPropTranslation)
  val engine = new TransformingDiffEngine(delegate, NoopTransformer, mockMap(translation))
  
  val o1 = new Object
  val o2 = new Object
  val ch1 = mockChange("ch1")
  val ch2 = mockChange("ch2")
  val ch3 = mockChange("ch3")
  val testDiff = Diff(o1, Self -> Diff(o1, ch1, ch2), Property("name") -> Diff(null, ch3))
  val translatedProp1 = mock[Property]
  val translatedProp2 = mock[Property]
  
  when(translation.translate(anyChange)).thenReturn(Seq(mockChange("translated")))
  when(delegate.calculateDiff(anyDiff, anyProp, any, any)).thenReturn(testDiff)
  
  
  test("should translate self changes") {
    engine.calculateDiff(o1, o2)
    
    verify(translation).translate(ch1)
    verify(translation).translate(ch2)
  }
  
  test("should keep a non-self change") {
    val d = engine.calculateDiff(o1, o2)
    
    d.leafChanges should contain (Path("name"), ch3)
  }
  
  test("should remove original changes") {
    val diff = engine.calculateDiff(o1, o2)
    
    val selfChanges = diff.changes(EmptyPath).get.leafChanges
    selfChanges should have size 2
    selfChanges.map(_._2) should not contain ch1
    selfChanges.map(_._2) should not contain ch2
  }
  
  test("should not translate nested changes") {
    engine.calculateDiff(o1, o2)
    
    verify(translation, never).translate(ch3)
  }
  
  test("should add all the result changes to the diff") {
    val ch4 = mockChange("ch4")
    val ch5 = mockChange("ch5")
    when(translation.translate(ch1)).thenReturn(Seq(ch4, ch5))
    
    val d = engine.calculateDiff(o1, o2)
    
    d should haveChange(ch4)
    d should haveChange(ch5)
  }
  
  test("should translate all the 1st level properties") {
    val engine = new TransformingDiffEngine(delegate, NoopTransformer, mockMap(translation), mockPropTranslations)
    
    when(translatedProp1.get(any)).thenReturn(Some(new Object))
    when(translatedProp2.get(any)).thenReturn(Some(new Object))
    when(mockPropTranslation.apply(of(Self), any)).thenReturn(translatedProp1)
    when(mockPropTranslation.apply(of(Property("name")), any)).thenReturn(translatedProp2)
    
    val d = engine.calculateDiff(o1, o2)
    
    d should haveDifference(translatedProp1)
    d should haveDifference(translatedProp2)
  }
  
  test("should transform a list-diff into set-diff") {
    val diff = Diff(o1, Deletion("b", 1), Insertion("a", 0), Shift("d", 4, 10), NewValue(Property("[3]"), "c", "C"))
    
    val engine = new TransformingDiffEngine(mockCoordinator(diff), NoopTransformer,
        Map(classOf[Shift] -> new ShiftToNothing,
          classOf[NewValue] -> new NewValueToRmAdd,
          classOf[Insertion] -> new InsertionToAddition,
          classOf[Deletion] -> new DeletionToRemoval))
    
    engine.calculateDiff(o1, o2) should haveChanges (Removal("b"), Addition("a"), Removal("c"), Addition("C"))
  }
}