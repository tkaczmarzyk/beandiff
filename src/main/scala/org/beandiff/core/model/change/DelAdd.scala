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


class DelAdd( // FIXME should 2 objects (Deletion and Addition) be used instead?
    private val deleted: Any,
    private val added: Any) extends Change {

  def perform(target: Any): Unit = {
    val collection = target.asInstanceOf[java.util.Collection[Any]]
    collection.remove(deleted)
    collection.add(added)
  }
  
  @deprecated
  def newValue: Any = added
  
  @deprecated
  def oldValue: Any = deleted
}