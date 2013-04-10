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

import java.lang.Number
import java.lang.Object
import org.beandiff.support.ObjectSupport.convert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.DescendantJavaBean
import org.beandiff.beans.SimpleJavaBean


@RunWith(classOf[JUnitRunner])
class ClassSupportTest extends FunSuite with ShouldMatchers {

  val descClass = classOf[DescendantJavaBean]
  val superClass = classOf[SimpleJavaBean]
  
  
  test("should include declared fields from the class itself") {
    val fields = new ClassSupport(descClass).fieldsInHierarchy
    
    fields should contain (descClass.getDeclaredField("nickname"))
  }
  
  test("should include declared fields from the superclass") {
    val fields = new ClassSupport(descClass).fieldsInHierarchy
    
    fields should contain (superClass.getDeclaredField("name"))
    fields should contain (superClass.getDeclaredField("value"))
  }
  
  test("should find fields from class, parent and grandparent") {
    val fields = new ClassSupport(classOf[GrandChild]).fieldsInHierarchy
    
    val numAllFields = classOf[GrandChild].getDeclaredFields.size +
      classOf[DescendantJavaBean].getDeclaredFields.size + classOf[SimpleJavaBean].getDeclaredFields.size
    
    fields should have size numAllFields
  }

  class GrandChild(name: String, value: Int, nickname: String) extends DescendantJavaBean(name, value, nickname) {
    private val test = "test"
  }
}