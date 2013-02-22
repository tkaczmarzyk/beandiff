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

import org.beandiff.beans.AnnoBean
import org.beandiff.beans.AnnoBean2
import org.beandiff.beans.AnnoChild
import org.beandiff.beans.DoubleIdBean
import org.beandiff.beans.MethodAnnoBean
import org.beandiff.beans.SimpleJavaBean
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.AnnoGrandChild
import org.beandiff.annotation.BeanId

@RunWith(classOf[JUnitRunner])
class AnnotationEqualityInvestigatorTest extends FunSuite with ShouldMatchers {

  private val eqInvestigator = new AnnotationEqualityInvestigator(classOf[BeanId])
  
  
  test("should consider 2 objects equal when annotated fields are equal") {
    val o1 = new AnnoBean("theId", "name")
    val o2 = new AnnoBean("theId", "otherName")
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("should not consider 2 object equal when annotated fields differ") {
    val o1 = new AnnoBean("id1", "name")
    val o2 = new AnnoBean("id2", "name")
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("should claim equality when all annotated fields are equal") {
    val o1 = new DoubleIdBean("id", 1, "name")
    val o2 = new DoubleIdBean("id", 1, "otherName")
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("should not claim equality when not all annotated fields are equal") {
    val o1 = new DoubleIdBean("id", 1, "name")
    val o2 = new DoubleIdBean("id", 2, "name")
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("should detect annotated getter") {
    val o1 = new MethodAnnoBean("id", "name")
    val o2 = new MethodAnnoBean("id", "otherName")
    
    eqInvestigator.areEqual(o1, o2) should be === true
  }
  
  test("should detect that annotated getters return different values") {
    val o1 = new MethodAnnoBean("id1", "name")
    val o2 = new MethodAnnoBean("id2", "name")
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("2 nulls should be equal") {
    eqInvestigator.areEqual(null, null) should be === true
  }
  
  test("null and non-null should not be equal") {
    val nonNull = new AnnoBean("id", "name")
    eqInvestigator.areEqual(null, nonNull) should be === false
    eqInvestigator.areEqual(nonNull, null) should be === false
  }
  
  test("should detect that it's the same object even if no annotation present") {
    val o1 = new SimpleJavaBean("a", 1)
    
    eqInvestigator.areEqual(o1, o1) should be === true
  }
  
  test("should claim not equal if no annotation found") {
    val o1 = new SimpleJavaBean("a", 1)
    val o2 = new SimpleJavaBean("a", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("instances of different types should not be equal") {
    val o1 = new AnnoBean("id", "name")
    val o2 = new AnnoBean2("id", "name")
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
  
  test("should detect difference in annotated fields from supertype") {
    val o1 = new AnnoChild("id1", 1)
    val o2 = new AnnoChild("id2", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === false    
  }
  
  test("should detect difference in annotated fields in subtype") {
    val o1 = new AnnoChild("id", 1)
    val o2 = new AnnoChild("id", 2)
    
    eqInvestigator.areEqual(o1, o2) should be === false    
  }
  
  test("should detect that fields from both sub and super type are equal") {
    val o1 = new AnnoChild("id", 1)
    val o2 = new AnnoChild("id", 1)
    
    eqInvestigator.areEqual(o1, o2) should be === true    
  }
  
  test("should detect differenct in annotated field in super super type") {
    val o1 = new AnnoGrandChild("id1", 1, 1)
    val o2 = new AnnoGrandChild("id2", 1, 1)
    
    eqInvestigator.areEqual(o1, o2) should be === false
  }
}