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

import java.util.Arrays
import org.beandiff.BeanDiff
import org.beandiff.TestDefs.anyDiff
import org.beandiff.TestDefs.mock
import org.beandiff.TestDefs.of
import org.beandiff.TestDefs.NameIsId
import org.beandiff.TestDefs.EverythingIsSimpleVal
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Property
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.test.BeanDiffMatchers.haveDifference
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.support.ClassDictionary
import org.beandiff.equality.ObjectType
import org.beandiff.equality.Entity

@RunWith(classOf[JUnitRunner])
class LcsDiffEngineTest extends FunSuite with ShouldMatchers {

  private val mockDelegate = mock[DiffEngineCoordinator]

  when(mockDelegate.calculateDiff(anyDiff, any[Property], any(), any())).thenAnswer(new Answer[Diff] {
    override def answer(invocation: InvocationOnMock) = invocation.getArguments()(0).asInstanceOf[Diff]
  })

  private val engine = new LcsDiffEngine(mockDelegate, EverythingIsSimpleVal, new NaiveLcsCalc())

  private val abc = Arrays.asList("a", "b", "c")
  private val xbc = Arrays.asList("x", "b", "c")
  private val bcd = Arrays.asList("b", "c", "d")
  private val bas = Arrays.asList("b", "a", "s")

  test("should detect that an has been modified even though its id is unchanged") {
    val a1 = new SimpleJavaBean("a", 1)
    val a2 = new SimpleJavaBean("a", 2)
    val b = new SimpleJavaBean("b", 0)
    val c = new SimpleJavaBean("c", 0)

    val l1 = JList(a1, b, c)
    val l2 = JList(a2, b, c)

    val engine = new LcsDiffEngine(BeanDiff.diffEngine().asInstanceOf[DiffEngineCoordinator],
      NameIsId, new NaiveLcsCalc()) // TODO simplify creation

    val d = engine.calculateDiff(l1, l2)
    
    d.leafChanges should have size 1
    d should haveDifference("[0].value")
  }

  test("should call delegate on elements in LCS") {
    val diff = engine.calculateDiff(abc, bcd)

    verify(mockDelegate).calculateDiff(anyDiff, of(Property("[1]")), of("b"), of("b"))
    verify(mockDelegate).calculateDiff(anyDiff, of(Property("[2]")), of("c"), of("c"))
  }

  test("should add insertions for elements at right but not in the LCS") {
    val diff = engine.calculateDiff(bcd, bas)

    diff.leafChanges should contain((EmptyPath, new Insertion("a", 1).asInstanceOf[Change])) // TODO eliminate asInstanceOf
    diff.leafChanges should contain((EmptyPath, new Insertion("s", 2).asInstanceOf[Change]))
  }

  test("should add deletions for elements at left but not in the LCS") {
    val diff = engine.calculateDiff(bcd, bas)

    diff.leafChanges should contain((EmptyPath, new Deletion("c", 1).asInstanceOf[Change])) // TODO eliminate asInstanceOf
    diff.leafChanges should contain((EmptyPath, new Deletion("d", 2).asInstanceOf[Change]))
  }
}