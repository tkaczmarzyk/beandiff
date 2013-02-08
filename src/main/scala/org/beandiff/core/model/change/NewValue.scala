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

import org.beandiff.core.model.Property


class NewValue(
  val target: Any, // TODO tmp // FIXME target vs (self->FlatCHangeSet[NewValue[1->2]) vs Insertion/Deletion (attached to collection rather to IndexProperty)
  val oldValue: Any, 
  val newValue: Any) extends Change with Equals {

  override def perform(target: Any): Unit = // FIXME target parameter is now not a real target :(
    throw new IllegalStateException()
  
  def perform(target: Any, prop: Property) =//FIXME temporary hack during refactoring
    prop.setValue(target, newValue)
    
  def canEqual(other: Any) = {
    other.isInstanceOf[NewValue]
  }
  
  override def equals(other: Any) = {
    other match {
      case that: NewValue => that.canEqual(NewValue.this) && oldValue == that.oldValue && newValue == that.newValue
      case _ => false
    }
  }
  
  override def hashCode() = {
    val prime = 41
    prime * (prime * (prime + oldValue.hashCode) + newValue.hashCode)
  }
  
  override def toString = "NewValue[" + oldValue + "->" + newValue + "]"

}