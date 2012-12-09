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

import java.util.Arrays
import org.beandiff.TestDefs.of
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Insertion
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.lcs.NaiveLcsCalc
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Deletion
import org.beandiff.core.model.Insertion


@RunWith(classOf[JUnitRunner])
class LcsDiffEngineTest extends FunSuite with ShouldMatchers {

  private val mockDelegate = mock(classOf[DiffEngine])
  
  when(mockDelegate.calculateDiff0(any(classOf[Diff]), any(classOf[Path]), any(), any())).thenAnswer(new Answer[Diff] {
    override def answer(invocation: InvocationOnMock): Diff = invocation.getArguments()(0).asInstanceOf[Diff]
  })
  
  private val engine = new LcsDiffEngine(mockDelegate, new NaiveLcsCalc(new StdEqualityInvestigator))
  
  
  private val abc = Arrays.asList("a", "b", "c")
  private val bcd = Arrays.asList("b", "c", "d")
  private val bas = Arrays.asList("b", "a", "s")
  
  
  test("should call dlegate on all elements in LCS") {
    val diff = engine.calculateDiff(abc, bcd)
    
    verify(mockDelegate).calculateDiff0(any(classOf[Diff]), of(Path.of("[1]")), of("b"), of("b"))
    verify(mockDelegate).calculateDiff0(any(classOf[Diff]), of(Path.of("[2]")), of("c"), of("c"))
  }
  
  test("should add insertions for elements at right but not in the LCS") {
    val diff = engine.calculateDiff(bcd, bas)
    val l = diff.leafChanges
    assert(diff.leafChanges.toList.contains(EmptyPath -> new Insertion("a", 1)))
    assert(diff.leafChanges.toList.contains(EmptyPath -> new Insertion("s", 2)))
  }
  
  test("should add deletions for elements at left but not in the LCS") {
    val diff = engine.calculateDiff(bcd, bas)
    
    assert(diff.leafChanges.toList.contains(EmptyPath -> new Deletion("c", 1)))
    assert(diff.leafChanges.toList.contains(EmptyPath -> new Deletion("d", 2)))
  }
}