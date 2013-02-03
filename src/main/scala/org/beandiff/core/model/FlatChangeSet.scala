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

import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change


class FlatChangeSet(
    val target: Any, // TODO decide whether it should be present
    private val path: Path, // FIXME now it's rather a Property: either change field type or fix the impl
    private val selfChanges: List[Change]) extends ChangeSet {

  
  def this(target: Any, path: Path, changes: Change*) {
    this(target, path, List(changes:_*))
  }
  
  
  override def leafChanges: Traversable[(Path, Change)] = selfChanges.map(ch => (path, ch))
  
  override def withChange(change: Change) = new FlatChangeSet(target, path, selfChanges :+ change)
  
  override def withChange(path: Path, change: Change): ChangeSet = { 
    if (path.depth == 0)
      withChange(change)
    else
      this.toDiff.withChange(path, change)
  }
  
  override def hasDifference(pathToFind: Path): Boolean = pathToFind == EmptyPath && !selfChanges.isEmpty
  
  private def toDiff = new DiffImpl(path, target, Map(Self -> this))
  
  override def toString() = "FlatChangeSet[" + selfChanges.mkString("", ", ", "") + "]"
  
  override def transformTarget = {
    for (change <- selfChanges) {
      change.perform(target)
    }
  }
}