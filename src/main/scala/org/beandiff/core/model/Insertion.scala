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

case class Insertion(
  private val element: Any,
  val index: Int) extends Change with Equals { // FIXME temp public

  type jList = java.util.List[Any] // FIXME

  override def perform(target: Any): Unit = {
    require(target.isInstanceOf[jList])
    val list = target.asInstanceOf[jList]
    list.add(index, element)
  }

  override def newValue = element // FIXME

  override def oldValue() = "_tmp_[NOTHING]"


  override def toString = "Insertion[" + element + ", " + index + "]"
  
  def canEqual(other: Any) = {
    other.isInstanceOf[org.beandiff.core.model.Insertion]
  }

  override def equals(other: Any) = {
    other match {
      case that: org.beandiff.core.model.Insertion => that.canEqual(Insertion.this) && element == that.element && index == that.index
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime * (prime + element.hashCode) + index.hashCode
  }
}