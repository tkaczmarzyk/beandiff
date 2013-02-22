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
package org.beandiff

import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.{Matchers => MockitoMatchers}
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.Property
import scala.collection.immutable.HashMap
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mockito


object TestDefs {

  implicit def fun0ToAnswer[R](fun: Function1[InvocationOnMock, R]) = {
    new Answer[R] {
      override def answer(invocation: InvocationOnMock) = fun.apply(invocation)
    }
  }
  
  implicit def fun1ToAnswer[R](fun: Function0[R]) = {
    new Answer[R] {
      override def answer(invocation: InvocationOnMock) = fun.apply()
    }
  }
  
  def mockChange() = mock[Change]
  
  def mockChange(name: String) = {
    Mockito.mock(classOf[Change], name)
  }
  
  def mockDiff() = {
    val diff = mock[Diff]
    when(diff.hasDifference).thenReturn(true)
    when(diff.hasDifference(any[Path])).thenReturn(true)
    diff
  }
  
  def mockMap[K, V](value: V) = {
    new HashMap[K, V] {
      override def apply(key: K) = value
      override def get(key: K) = Some(value)
    }
  }
  
  def mock[T](implicit m: Manifest[T]): T = {
    Mockito.mock(m.erasure).asInstanceOf[T]
  }
  
  def mock[T](name: String)(implicit m: Manifest[T]): T = {
    Mockito.mock(m.erasure, name).asInstanceOf[T]
  }
  
  def anyDiff = any[Diff]
  
  def anyChange = any[Change]
  
  def anyProp = any[Property]
  
  def anyPath = any[Path]
  
  def any[T](implicit m: Manifest[T]): T = {
    MockitoMatchers.any(m.erasure).asInstanceOf[T]
  }
  
  def of[T](o: T) = MockitoMatchers.eq(o)
}