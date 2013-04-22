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
package org.beandiff.core.model

import org.beandiff.TypeDefs.JSet
import org.beandiff.support.ClassDictionary


case class ElementProperty(element: Any) extends Property {

  override def get(src: Any) = {
    src match {
      case set: JSet if set.contains(element) => Some(element)
      case _ => None
    }
  }
  
  override def setValue(target: Any, value: Any) = {
    throw new UnsupportedOperationException("ElementProperty.setValue")
  }
  
  override def mkString: String = "[]"
    
  override def mkString(dict: ClassDictionary[(Any => String)]): String = {
    val toStr = if (element == null) dict.defaultValue else dict(element.getClass) // TODO move to ClassDictionary ?
    "[" + toStr(element) + "]"
  }
}