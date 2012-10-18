/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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
package org.beandiff.core

import org.beandiff.support.ObjectSupport._

class FieldProperty(val name: String) extends Property {

  override def value(o: Any): Any = {
    if (o hasField name)
      o getFieldVal name
    else null
  }
  
  override def equals(other: Any) = {
    other match {
      case that: FieldProperty => name == that.name
      case _ => false
    }
  }
  
  override def hashCode() = {
    name.hashCode
  }
  
  override def toString() = {
    name
  }
}