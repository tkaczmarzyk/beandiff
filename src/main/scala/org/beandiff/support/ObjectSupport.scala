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

import java.lang.reflect.Field


object ObjectSupport {
  
  implicit def convert(o: Any) = new ObjectSupport(o)
}

class ObjectSupport(val target: Any) {
  
  def hasField(name: String) = {
    target.getClass.getDeclaredFields().contains((f: Field) => f.getName == name)
  }
  
  def getFieldVal(name: String) = {
    val f = target.getClass.getDeclaredField(name)
    f.setAccessible(true)
    f.get(target)
  }
  
  def apply(index: Int) = target.asInstanceOf[java.util.List[_]].get(index).asInstanceOf[Object]
}