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

import org.beandiff.core.model.change.Change


object ChangeSet {
  
  def apply(target: Any, path: Path): ChangeSet = new FlatChangeSet(target, path)
  
  def apply(target: Any, path: Path, changes: Change*): ChangeSet = new FlatChangeSet(target, path, changes:_*)
}

trait ChangeSet {

  def leafChanges: Traversable[(Path, Change)]
  
  def withChange(change: Change): ChangeSet
  
  def withChange(path: Path, change: Change): ChangeSet
  
  def hasDifference(pathToFind: Path): Boolean
  
  def transformTarget(): Unit
  
  def target: Any // TODO remove?
}