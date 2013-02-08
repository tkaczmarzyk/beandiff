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
  val target: Any, // TODO decide whether it should be prese, 
  private val selfChanges: List[Change]) extends Diff {

  def this(target: Any, changes: Change*) { // TODO Change parameter to ensure at least 1 change is provided
    this(target, List(changes: _*))
  }

  override def leafChanges: Traversable[(Path, Change)] = selfChanges.map(ch => (Path(Self), ch))

  override def withChange(change: Change) = withChange(Self, change)

  override def withChange(path: Path, change: Change): Diff = {
    if (path.depth <= 1)
      withChange(path.head, change)
    else
      toDiff.withChange(path, change)
  }

  override def hasDifference(pathToFind: Path): Boolean = pathToFind == EmptyPath && !selfChanges.isEmpty

  override def changes(path: Path): Diff = { // TODO is it really needed ? (should be ever called?)
    if (path != EmptyPath)
      throw new IllegalArgumentException
    else
      FlatDiff.this // FIXME Self-related confusion
  }

  override def withChanges(path: Path, changes: Diff): Diff = {
    if (path.depth <= 1)
      withChanges(path.head, changes)
    else
      toDiff.withChanges(path, changes)
  }

  override def without(prop: Property) = {
    without(Path(prop))
  }
  
  override def without(path: Path) = {
    if (path == EmptyPath)
      new FlatDiff(target)
    else
      throw new IllegalArgumentException()
  }

  override def withChange(prop: Property, change: Change) = {
    if (prop == Self)
      new FlatDiff(target, change :: selfChanges)
    else
      toDiff.withChange(prop, change)
  }

  override def withChanges(prop: Property, diff: Diff) = { // FIXME it's now based on some assumptions (e.g. that Self cannot be mapped to anything but FlatDiff)
    if (selfChanges.isEmpty)
      diff
    else if (diff.changes.exists(pathDiff => pathDiff._1 != EmptyPath))
      toDiff.withChanges(prop, diff)
    else
      new FlatDiff(target, selfChanges ++ diff.leafChanges.map(_._2))
  }

  override def hasDifference = !selfChanges.isEmpty

  override def hasDifference(pathStr: String) = toDiff.hasDifference(pathStr)

  def toDiff =
    if (selfChanges.isEmpty)
      Diff(target)
    else
      Diff(target, Map(Self -> FlatDiff.this))

  override def toString() = "FlatDiff[" + selfChanges.mkString("", ", ", "") + "]"

  override def changes = toDiff.changes

  override def hashCode = 13 * target.hashCode + selfChanges.hashCode
  
  override def equals(other: Any) = {
    other match {
      case that: FlatDiff => target == that.target && selfChanges == that.selfChanges
      case _ => false
    }
  }
  
  override def transformTarget = {
    for (change <- selfChanges) {
      change.perform(target)
    }
  }
}