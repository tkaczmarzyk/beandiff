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


class FlatDiff(
    val target: Any,  // TODO decide whether it should be prese, 
    private val selfChanges: List[Change]) extends Diff {
  
  def this(target: Any, changes: Change*) {
    this(target, List(changes:_*))
  }
  
  
  override def leafChanges: Traversable[(Path, Change)] = selfChanges.map(ch => (Path(Self), ch))
  
  override def withChange(change: Change) = new FlatDiff(target, selfChanges :+ change)
  
  override def withChange(path: Path, change: Change): Diff = { 
    if (path.depth == 0)
      withChange(change)
    else
      FlatDiff.this.toDiff.withChange(path, change)
  }
  
  override def hasDifference(pathToFind: Path): Boolean = pathToFind == EmptyPath && !selfChanges.isEmpty

  override def without(path: Path) = {
    if (path == EmptyPath)
      new FlatDiff(target, List())
    else
      throw new IllegalArgumentException()
  }
  
  override def changes(path: Path): Diff = { // TODO is it really needed ? (should be ever called?)
    if (path != EmptyPath)
      throw new IllegalArgumentException
    else
      FlatDiff.this // FIXME Self-related confusion
  }
  
  override def withChanges(path: Path, changes: Diff): Diff = { // TODO verify
    toDiff.withChanges(path, changes)
  }
  
  override def without(prop: Property) = {
    FlatDiff.this // FIXME FIXME FIXME
  }
  
  override def withChange(prop: Property, change: Change) = 
    toDiff.withChange(prop, change)
  
  override def withChanges(prop:Property, diff: Diff) =
    toDiff.withChanges(prop, diff)// FIXME FIXME FIXME
    
  override def hasDifference = !selfChanges.isEmpty
  
  override def hasDifference(pathStr: String) = toDiff.hasDifference(pathStr)
    
  def toDiff =
    if (selfChanges.isEmpty)
      Diff(target)
    else
      Diff(target, Map(Self -> FlatDiff.this))
  
  override def toString() = "FlatDiff[" + selfChanges.mkString("", ", ", "") + "]"
  
  override def changes = toDiff.changes
  
  override def transformTarget = {
    for (change <- selfChanges) {
      change.perform(target)
    }
  }
}