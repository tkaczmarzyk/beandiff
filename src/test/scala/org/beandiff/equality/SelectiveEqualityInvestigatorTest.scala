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
package org.beandiff.equality

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.beans.ParentBean

@RunWith(classOf[JUnitRunner])
class SelectiveEqualityInvestigatorTest extends FunSuite with ShouldMatchers {

  test("should ignore not listed fields") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new SimpleJavaBean("aaa", 2)
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("should not claim equality when listed fields differ") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new SimpleJavaBean("bbb", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("should consider equal when both listed fields are equal") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name", "value")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new SimpleJavaBean("aaa", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("should not consider equal when not all of the listed fields are equal") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name", "value")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new SimpleJavaBean("aaa", 2)
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("should compare a property of nested elements") {
    val eqInvestigator = new SelectiveEqualityInvestigator("child.name")
    val o1 = new ParentBean("homer", new SimpleJavaBean("bart", 10))
    val o2 = new ParentBean("marge", new SimpleJavaBean("bart", 10))
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("identical objects should be considered equal even though no property found") {
    val eqInvestigator = new SelectiveEqualityInvestigator("not_existing")
    val o1 = new SimpleJavaBean("aaa", 1)
    
    eqInvestigator.areEqual(o1, o1) should be === true
  }
  
  test("objects of different classes should not be equal even though the values on path are the same") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new ParentBean("aaa")
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("two nulls should be equal") {
    new SelectiveEqualityInvestigator("name").areEqual(null, null) should be === true
  }
  
  test("null and non-null should not be equal") {
    val eqInvestigator = new SelectiveEqualityInvestigator("name")
    val o1 = new SimpleJavaBean("aaa", 1)
    
    eqInvestigator.areEqual(o1, null) should be === false
    eqInvestigator.areEqual(null, o1) should be === false
  }
  
  test("when objs don't have a value on the path, then should claim not equal") {
    val eqInvestigator = new SelectiveEqualityInvestigator("not_existing")
    val o1 = new SimpleJavaBean("aaa", 1)
    val o2 = new SimpleJavaBean("aaa", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
}