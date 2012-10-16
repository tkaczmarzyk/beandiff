/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 * 
 * This file is part of BeanDiff.
 * 
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
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

import org.beandiff.support.ClassSupport._
import scala.collection.mutable.HashMap
import com.sun.org.apache.xerces.internal.jaxp.DefaultValidationErrorHandler

class ClassDictionary[T](val defaultValue: T) {

  private val map = new HashMap[Class[_], T]

  def this(defaultValue: T, values: Iterable[(Class[_], T)]) = {
    this(defaultValue)
    values foreach {
      map += _
    }
  }

  def this(defaultValue: T, values: (Class[_], T)*) = {
    this(defaultValue, values.toList)
  }
  
  def withDefault(defaultValue: T): ClassDictionary[T] = {
    new ClassDictionary[T](defaultValue, map.toIterable)
  }

  def apply(c: Class[_]): T = {
    if (map.contains(c))
      map(c)
    else {
      val supportedSuperTypes = c.allSuperTypes.filter(map.contains(_))
      if (!supportedSuperTypes.isEmpty)
        map(supportedSuperTypes.head)
      else
        defaultValue
    }
  }
}