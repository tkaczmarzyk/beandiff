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

import Path.EmptyPath
import org.beandiff.core.model.change.Change
import scala.annotation.tailrec


private[model] class DeepDiff(
  val target: Any,
  private val propChanges: Map[Property, Diff]) extends Diff {

  override def leafChanges: Traversable[(Path, Change)] = // TODO generic method for traversation (with break option)
    propChanges.toList.flatMap({
      case (prop, changeSet) => changeSet.leafChanges.map(pathChange => (Path(prop) ++ pathChange._1, pathChange._2))
    })

  override def changes = propChanges.toList

  override def withChange(change: Change): DeepDiff = withChange(Self, change)
  
  override def withChange(property: Property, change: Change): DeepDiff = {
    val newMod = propChanges.get(property) match {
      case Some(mod) => mod.withChange(change)
      case None => new FlatDiff(property.value(target), change)
    }

    new DeepDiff(target, propChanges + (property -> newMod))
  }

  override def withChanges(property: Property, changes: Diff) =
    new DeepDiff(target, propChanges + (property -> changes))

  override def withChange(path: Path, change: Change): Diff = {
    if (path.depth <= 1) {
      withChange(path.head, change)
    } else {
      val interChangeset = propChanges.get(path.head) match {
        case Some(changeset) => changeset
        case None => new DeepDiff(path.head.value(target), Map())
      }

      withChanges(path.head, interChangeset.withChange(path.tail, change))
    }
  }
  
  override def without(prop: Property) = {
    new DeepDiff(target, propChanges - prop)
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

  override def changes(path: Path): Diff = {
    if (path.depth <= 1)
      propChanges(path.head)
    else
      propChanges(path.head).changes(path.tail)
  }
  
  override def without(path: Path): Diff = {
    if (path == EmptyPath)
      Diff(target)
    else if (path.depth == 1)
      without(path.head)
    else {
      val subWithout = propChanges(path.head).without(path.tail)
      if (subWithout.leafChanges.isEmpty)
        without(path.head)
      else
        without(path.head).withChanges(path.head, subWithout)
    }
  }
  
  override def withChanges(path: Path, changes: Diff): Diff = {
    if (path.depth <= 1)
      withChanges(path.head, changes)
    else {
      val interChangeset = propChanges.get(path.head) match { // FIXME FIXME FIXME temporary copy-paste
        case Some(changeset) => changeset
        case None => new DeepDiff(path.head.value(target), Map())
      }
      
      without(path.head)
      	.withChanges(path.head, interChangeset.withChanges(path.tail, changes))
    }
  }
  
  override def transformTarget() = {
    propChanges.foreach({
      case (prop, changeSet) => changeSet.transformTarget()
    })
  }

  override def hashCode = 13 * target.hashCode + propChanges.hashCode
  
  override def equals(other: Any) = {
    other match {
      case that: DeepDiff => that.target == target && that.propChanges == propChanges
      case _ => false
    }
  }
  
  override def toString = "DeepDiff[" + propChanges.mkString("", ",", "") + "]"
}