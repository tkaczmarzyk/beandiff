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
package org.beandiff.support

import org.beandiff.annotation.BeanId
import org.beandiff.beans.AnnoGrandChild
import org.beandiff.support.AnnotationUtil.enrich
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.MethodAnnoChild


@RunWith(classOf[JUnitRunner])
class AnnotationUtilTest extends FunSuite with ShouldMatchers {

  object TestObj {
    @BeanId
    private val name = "Tester"
    
    private val lastname = "McBean"
      
    @BeanId
    private val age = 102
    
    @BeanId
    def fullname() = name + " " + lastname
    
    def tripleAge() = 306
    
    @BeanId
    def shortName(len: Int) = name.substring(0, len)
  }
  
  test("should return annotated fields") {
    TestObj.annotatedFields[BeanId] should have size 2
    TestObj.annotatedFields[BeanId] should contain (TestObj.getClass.getDeclaredField("name"))
    TestObj.annotatedFields[BeanId] should contain (TestObj.getClass.getDeclaredField("age"))
  }
  
  test("all fields should be made accessible") {
    TestObj.annotatedFields[BeanId].filter(!_.isAccessible()) should be ('empty)
  }
  
  test("all methods should be made accessible")	{
    TestObj.annotatedMethods[BeanId].filter(!_.isAccessible()) should be ('empty)
  }
  
  test("should return annotated parameterless non-void methods") {
    TestObj.annotatedMethods[BeanId] should have size 1
    
    TestObj.annotatedMethods[BeanId] should contain (TestObj.getClass.getDeclaredMethod("fullname"))
  }
  
  test("should find annotated fields from super types") {
    new AnnoGrandChild().annotatedFields[BeanId] should have size 3
  }
  
  test("should find annotated methods from super type") {
    new MethodAnnoChild().annotatedMethods[BeanId] should have size 2
  }
}