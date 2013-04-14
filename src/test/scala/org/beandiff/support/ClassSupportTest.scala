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
import org.beandiff.support.ObjectSupport.RichObject
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.DescendantJavaBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.support.ClassSupport.RichClass
import java.util.NavigableSet
import java.util.TreeSet
import java.util.SortedSet
import java.util.Collection


@RunWith(classOf[JUnitRunner])
class ClassSupportTest extends FunSuite with ShouldMatchers {

  val descClass = classOf[DescendantJavaBean]
  val superClass = classOf[SimpleJavaBean]
  
  
  test("should include declared fields from the class itself") {
    val fields = descClass.fieldsInHierarchy
    
    fields should contain (descClass.getDeclaredField("nickname"))
  }
  
  test("should make all fields accessible") {
    val fields = superClass.fieldsInHierarchy
    
    fields.filter(!_.isAccessible()) should be ('empty)
  }
  
  test("should skip static fields") {
    val fields = superClass.fieldsInHierarchy
    
    fields should not contain (superClass.getDeclaredField("orderByName"))
  }
  
  test("should include declared fields from the superclass") {
    val fields = descClass.fieldsInHierarchy
    
    fields should contain (superClass.getDeclaredField("name"))
    fields should contain (superClass.getDeclaredField("value"))
  }
  
  test("should map fieldnames to fields") {
    val fieldByName = descClass.fieldsInHierarchyByName
    
    fieldByName should be === Map("name" -> superClass.getDeclaredField("name"),
        "value" -> superClass.getDeclaredField("value"), "nickname" -> descClass.getDeclaredField("nickname"))
  }
  
  test("should find fields from class, parent and grandparent") {
    val fields = classOf[GrandChild].fieldsInHierarchy
    
    fields should have size 4
  }
  
  test("should find interfaces from all super levels") {
    val superTypes = classOf[TreeSet[_]].allSuperTypes
    
    assert(superTypes.contains(classOf[NavigableSet[_]]))
    assert(superTypes.contains(classOf[SortedSet[_]]))
    assert(superTypes.contains(classOf[java.util.Set[_]]))
    assert(superTypes.contains(classOf[Collection[_]]))
    assert(superTypes.contains(classOf[java.lang.Iterable[_]]))
  }
}

class GrandChild(name: String, value: Int, nickname: String) extends DescendantJavaBean(name, value, nickname) {
  private val test = "test"
}