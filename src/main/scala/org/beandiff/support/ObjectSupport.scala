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

import java.lang.Object
import java.lang.reflect.Field
import org.beandiff.support.ClassSupport.RichClass

object ObjectSupport {

  implicit class RichObject(val target: Any) extends AnyVal {

    def hasField(name: String) = {
      new RichClass(target.getClass).hasField(name)
    }

    def getFieldVal(name: String) = {
      getField(name).get(target)
    }

    def getField(name: String) = {
      val f = target.getClass.findField(name)
      f.setAccessible(true)
      f
    }

    def setFieldVal(fieldName: String, value: Any) = {
      getField(fieldName).set(target, value)
    }

    def allClasses: List[Class[_]] = {
      def allClasses(c: Class[_]): List[Class[_]] = {
        if (c == classOf[Object])
          List(c)
        else
          c :: allClasses(c.getSuperclass())
      }
      allClasses(target.getClass)
    }

    def apply(index: Int) = target.asInstanceOf[java.util.List[_]].get(index).asInstanceOf[Object]
  }
}