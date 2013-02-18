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
import org.beandiff.TestDefs.of
import org.beandiff.beans.IdBean
import org.beandiff.beans.ParentBean
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.invocation.InvocationOnMock
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


@RunWith(classOf[JUnitRunner])
class DelegatingDiffEngineTest extends FunSuite with ShouldMatchers {

  val descStrategy = mock(classOf[DescendingStrategy])
  val engine = new DelegatingDiffEngine(BeanDiff.DefaultEqInvestigators, descStrategy)
  
  when(descStrategy.shouldProceed(anyPath, any, any))
  	.thenAnswer((inv: InvocationOnMock) => 
  	  !List(classOf[String], classOf[Int], classOf[Integer]).contains(inv.getArguments()(1).getClass))
  
  
  test("should ask descendingStrategy with all the paths") {
    val p1 = new ParentBean("p1", new IdBean(1))
    val p2 = new ParentBean("p2", new IdBean(2))
    
    val diff = engine.calculateDiff(p1, p2)
    
    verify(descStrategy).shouldProceed(of(EmptyPath), any, any)
    verify(descStrategy).shouldProceed(of(Path("name")), any, any)
    verify(descStrategy).shouldProceed(of(Path("child")), any, any)
    verify(descStrategy).shouldProceed(of(Path("child.id")), any, any)
  }
}