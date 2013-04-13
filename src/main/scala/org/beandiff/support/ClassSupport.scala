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

import ClassSupport.convert
import java.lang.reflect.Field
import org.beandiff.support.FieldSupport.enrichField


object ClassSupport {
  implicit def convert(c: Class[_]): ClassSupport = new ClassSupport(c)
}

class ClassSupport(c: Class[_]) {

  def fieldsInHierarchy: List[Field] = {
    val fields = c.getDeclaredFields.toList.map {
      f => f.setAccessible(true); f
    }
    val allFields = 
      if (c.getSuperclass() == null) fields
      else fields ++ c.getSuperclass.fieldsInHierarchy
    
    allFields.filter(!_.isStatic)
  }
  
  def fieldsInHierarchyByName: Map[String, Field] = {
    fieldsInHierarchy.map(f => f.getName -> f).toMap
  }
  
  def hasField(field: String) = findField(field) != null
  
  def findField(field: String) = {
    def getField(clazz: Class[_]): Field = {
      val fields = clazz.getDeclaredFields().filter(_.getName() == field)
      if (!fields.isEmpty)
        fields.head
      else if (clazz.getSuperclass != null)
        getField(clazz.getSuperclass)
      else null
    }
    getField(c)
  }
  
  def allSuperTypes: List[Class[_]] = {
    if (c == classOf[Object])
      List()
    else {
      List(c.getSuperclass()) ++ c.getInterfaces() ++
        allSuperInterfaces(c.getInterfaces()) ++
        c.getSuperclass().allSuperTypes
    }
  }

  private def allSuperInterfaces(interfaces: Array[Class[_]]): List[Class[_]] = {
    if (interfaces.isEmpty)
      List()
    else
      interfaces.head.getInterfaces().toList ++ allSuperInterfaces(interfaces.tail)
  }
}