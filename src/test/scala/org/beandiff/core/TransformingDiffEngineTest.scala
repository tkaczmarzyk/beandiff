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

import org.beandiff.TestDefs._
import org.beandiff.TestDefs.of
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.test.BeanDiffMatchers._
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Self
import org.beandiff.core.model.Property
import scala.collection.immutable.HashMap
import org.mockito.invocation.InvocationOnMock
import org.beandiff.core.model.Path


@RunWith(classOf[JUnitRunner])
class TransformingDiffEngineTest extends FunSuite with ShouldMatchers {
  
  private val delegate = mock(classOf[DiffEngineCoordinator])
  private val translation = mock(classOf[ChangeTranslation])
  private val engine = new TransformingDiffEngine(delegate, NoopTransformer, mockMap(translation))
  
  private val o1 = new Object
  private val o2 = new Object
  private val ch1 = mockChange("ch1")
  private val ch2 = mockChange("ch2")
  private val ch3 = mockChange("ch3")
  private val testDiff = Diff(o1, Self -> Diff(o1, ch1, ch2), Property("name") -> Diff(null, ch3))
  
  when(translation.translate(anyChange)).thenAnswer(() => mockChange("translated"))
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
}