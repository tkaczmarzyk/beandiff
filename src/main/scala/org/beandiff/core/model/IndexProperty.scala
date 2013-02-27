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

import org.beandiff.TypeDefs.JList
import org.beandiff.support.ObjectSupport._


case class IndexProperty(val index: Int) extends Property {
  
  override def value(o: Any) = {
    if (o.isInstanceOf[JList])
      o(index)
    else null //TODO
  }
  
  override def get(o: Any) = {
    if (o.isInstanceOf[JList])
      Some(o(index))
    else None
  }
  
  override def setValue(target: Any, value: Any) = {
    if (target.isInstanceOf[JList]) {
      target.asInstanceOf[JList].set(index, value)
    } else {
      throw new IllegalArgumentException("expected List but was: " + target)
    }
  }
  
  override def equals(other: Any) = {
    other match {
      case that: IndexProperty => index == that.index
      case _ => false
    }
  }
  
  override def hashCode() = {
    index.hashCode
  }
  
  override def toString() = mkString
  
  override def mkString = "[" + index + "]"
}