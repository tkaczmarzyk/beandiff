/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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

import Path.EmptyPath

class DiffImpl(
  private val path: Path, // TODO redundant (as is stored in map)
  val target: Any,
  private val propChanges: Map[Property, ChangeSet]) extends Diff {

  def this(target: Any, changes: Map[Property, ChangeSet]) =
    this(EmptyPath, target, changes)

  override def leafChanges: Traversable[(Path, Change)] = // TODO generic method for traversation (with break option)
    propChanges.toList.flatMap({
      case (prop, changeSet) => changeSet.leafChanges.map(pathChange => (path ++ pathChange._1, pathChange._2))
    })

  override def changes = propChanges.toList

  override def withChange(change: Change): DiffImpl = withChange(Self, change)
  
  override def withChange(property: Property, change: Change): DiffImpl = {
    val newMod = propChanges.get(property) match {
      case Some(mod) => mod.withChange(change)
      case None => new FlatChangeSet(Path(property), change)
    }

    new DiffImpl(path, target, propChanges + (property -> newMod))
  }

  override def withChanges(property: Property, changes: ChangeSet) =
    new DiffImpl(path, target, propChanges + (property -> changes))

  override def withChange(path: Path, change: Change): Diff = {
    if (path.depth <= 1) {
      withChange(path.head, change)
    } else {
      val interChangeset = propChanges.get(path.head) match {
        case Some(changeset) => changeset
        case None => new DiffImpl(new PathImpl(path.head), null, Map()) // TODO null
      }

      withChanges(path.head, interChangeset.withChange(path.tail, change))
    }
  }
  
  override def without(prop: Property) = {
    new DiffImpl(path, target, propChanges - prop)
  }

  override def hasDifference(): Boolean =
    !propChanges.isEmpty

  override def hasDifference(pathStr: String): Boolean =
    hasDifference(Path.of(pathStr))

  override def hasDifference(pathToFind: Path): Boolean = {
    if (pathToFind == EmptyPath)
      hasDifference
    else
      propChanges.get(pathToFind.head) match {
        case Some(changeset) => changeset.hasDifference(pathToFind.tail)
        case None => false
      }
  }

  override def transformTarget() = { // FIXME FIXME FIXME
    propChanges.foreach({
      case (prop, changeSet) => changeSet.leafChanges.foreach({
        case (prop, change) => change.perform(target)
      })
    })
  }

  override def toString = "Diff[" + propChanges.mkString("", ",", "") + "]"
}