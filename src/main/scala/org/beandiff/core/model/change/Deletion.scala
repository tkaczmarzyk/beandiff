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
package org.beandiff.core.model.change

import org.beandiff.TypeDefs._
import org.beandiff.core.model.IndexProperty


case class Deletion(
  deleted: Any, // TODO it's only for presentation -- eliminate?, 
  index: Int) extends Change with Equals {

  override def perform(target: Any): Unit = {
    target.asInstanceOf[JList].remove(index)
  }

  override def targetProperty = new IndexProperty(index)
  
  override def newValue = None

  override def oldValue = Some(deleted)

  override def toString = "Deletion[" + deleted + ", " + index + "]"

  override def canEqual(other: Any) = {
    other.isInstanceOf[Deletion]
  }

  override def equals(other: Any) = {
    other match {
      case that: Deletion => that.canEqual(Deletion.this) && deleted == that.deleted && index == that.index
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime * (prime + deleted.hashCode) + index.hashCode
  }
}