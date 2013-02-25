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

import org.beandiff.BeanDiff
import org.beandiff.TestDefs.anyPath
import org.beandiff.TestDefs.fun0ToAnswer
import org.beandiff.TestDefs.mock
import org.beandiff.TestDefs.mockDescStrategy
import org.beandiff.beans.IdBean
import org.beandiff.beans.ParentBean
import org.mockito.Mockito
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class CompositeDescendingStrategyTest extends FunSuite with ShouldMatchers {

  private val strategy1 = mockDescStrategy(true)
  private val strategy2 = mockDescStrategy(true)
  private val strategy3 = mockDescStrategy(true)
  private val allOf = CompositeDescendingStrategy.allOf(strategy1, strategy2, strategy3)

  val path = mock[Path]
  val o1 = mock()
  val o2 = mock()

  test("should allow to proceed if all sub-strategies allow") {
    allOf.shouldProceed(path, o1, o2) should be === true

    verify(strategy1).shouldProceed(path, o1, o2)
    verify(strategy2).shouldProceed(path, o1, o2)
    verify(strategy3).shouldProceed(path, o1, o2)
  }

  test("should not allow to proceed if any of the sub-strategies disallow") {
    when(strategy2.shouldProceed(anyPath, any, any)).thenReturn(false)

    allOf.shouldProceed(mock[Path], mock(), mock()) should be === false
  }

  test("should check sub-strategies in order") {
    allOf.shouldProceed(path, o1, o2)

    val ord = Mockito.inOrder(strategy1, strategy2, strategy3)
    ord.verify(strategy1).shouldProceed(path, o1, o2)
    ord.verify(strategy2).shouldProceed(path, o1, o2)
    ord.verify(strategy3).shouldProceed(path, o1, o2)
  }
}