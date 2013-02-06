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

import org.beandiff.support.ObjectSupport._
import org.beandiff.core.TransformedProperty


class FieldProperty(val name: String) extends Property with Equals {

  override def value(o: Any): Any = {
    if (o == null) throw new IllegalArgumentException("cannot get field '" + name + "' from null")
    if (o hasField name)
      o getFieldVal name
    else null
  }
  
  override def setValue(target: Any, value: Any) = {
    if (target hasField name)
      target.setFieldVal(name, value)
    else {
      throw new IllegalArgumentException(target + " doesn't have field " + name)
    }
  }
  
  override def toString() = {
    name
  }
  
  def canEqual(other: Any) = {
    other.isInstanceOf[FieldProperty]
  }
  
  override def equals(other: Any) = {
    other match {
      case that: FieldProperty => that.canEqual(FieldProperty.this) && name == that.name
      case other: TransformedProperty => other.equals(this) // FIXME FIXME FIXME !
      case _ => false
    }
  }
  
  override def hashCode() = {
    val prime = 41
    prime + name.hashCode
  }
}