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

  override def without(prop: Property) = {
    val newPropChanges = propChanges - prop
    if (newPropChanges.isEmpty)
      Diff(target)
    else
      new DeepDiff(target, propChanges - prop)
  }

  override def without(path: Path, change: Change): Diff = {// TODO verify // TODO detect when it should become a FlatDiff
    if (path == EmptyPath)
      new DeepDiff(target, propChanges + (Self -> propChanges(Self).without(EmptyPath, change)))
    else
      without(path.head).withChanges(path.head, propChanges(path.head).without(path.tail, change)) // TODO simplity/decompose
  }
  
  def hasDifference(): Boolean =
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
  
  override def withChanges(property: Property, subDiff: Diff) = {
    if (!subDiff.hasDifference)
      this
    else {
      val existing = interChangeset(property)
      val merged = {
        if (!existing.hasDifference) subDiff
        else subDiff.leafChanges.foldLeft(existing)(
            (acc: Diff, pathChange: (Path, Change)) => acc.withChange(pathChange._1, pathChange._2))
      }
      new DeepDiff(target, propChanges + (property -> merged))
    }
  }
  
  override def withChanges(path: Path, changes: Diff): Diff = {
    if (path.depth <= 1)
      withChanges(path.head, changes)
    else
      without(path.head).withChanges(path.head, interChangeset(path.head).withChanges(path.tail, changes))
  }
  
  override def withChange(path: Path, change: Change): Diff = {
    if (path.depth <= 1)
      withChange(path.head, change)
    else
      without(path.head).withChanges(path.head, interChangeset(path.head).withChange(path.tail, change))
  }
  
  private def interChangeset(property: Property): Diff = {
    propChanges.get(property) match {
      case Some(diff) => diff
      case None => new DeepDiff(property.value(target), Map())
    }
  }
  
  override def withChange(property: Property, change: Change): DeepDiff = {
    val newMod = propChanges.get(property) match {
      case Some(mod) => mod.withChange(change)
      case None => new FlatDiff(property.value(target), change)
    }

    new DeepDiff(target, propChanges + (property -> newMod))
  }
  
  override def withChange(change: Change): DeepDiff = withChange(Self, change)
  
  override def transformTarget() = {
    propChanges.foreach({
      case (prop, changeSet) => changeSet.transformTarget(target, prop)
    })
  }

  override def hashCode = 13 * target.hashCode + propChanges.hashCode
  
  override def equals(other: Any) = {
    other match {
      case that: DeepDiff => that.target == target && that.propChanges == propChanges
      case _ => false
    }
  }
  
  override def forTarget(newTarget: Any) = new DeepDiff(newTarget, propChanges)
  
  override def toString = "DeepDiff[" + propChanges.mkString("", ",", "") + "]"
  
  def transformTarget(target: Any, prop: Property) = transformTarget()
}