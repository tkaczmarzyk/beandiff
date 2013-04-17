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
import org.beandiff.TypeDefs.JSet
import scala.collection.mutable.ArrayLike
import java.util.ArrayList
import collection.JavaConversions._

private[model] class DeepDiff(
  val target: Any,
  private val propChanges: Map[Property, Diff]) extends Diff {

  override def leafChanges = // TODO generic method for traversation (with break option)
    propChanges.toList.flatMap({
      case (prop, changeSet) => changeSet.leafChanges.map(pathChange => (Path(prop) ++ pathChange._1, pathChange._2))
    })

  override def changes = propChanges.toList

  override def without(prop: Property) = { // TODO reduce model complexity -- now the property can be map's key but also targetProperty of a self-change
    if (propChanges.contains(prop)) {
      val newPropChanges = propChanges - prop
      if (newPropChanges.isEmpty) Diff(target)
      else new DeepDiff(target, newPropChanges)
    } else if (propChanges.contains(Self)) {
      val reducedSelf = propChanges(Self).without(prop)
      if (!reducedSelf.hasDifference)
        this.without(Self)
      else
        this.without(Self).withChanges(Self, reducedSelf)
    } else this
  }

  override def without(path: Path, change: Change): Diff = { // TODO verify // TODO detect when it should become a FlatDiff
    propChanges.get(path.head) match {
      case None => this
      case Some(subDiff) =>
        if (path == EmptyPath)
          new DeepDiff(target, propChanges + (Self -> subDiff.without(EmptyPath, change)))
        else
          without(path.head).withChanges(path.head, subDiff.without(path.tail, change))
    }
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
        case None => propChanges.get(Self) match {
          case None => false
          case Some(diff) => diff.hasDifference(pathToFind)
        }
      }
  }

  override def changes(path: Path): Option[Diff] = {
    propChanges.get(path.head) match {
      case Some(subDiff) =>
        if (path.depth <= 1) Some(subDiff)
        else subDiff.changes(path.tail)
      case None => None
    }
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

  override def withChanges(path: Path, changes: Seq[Change]) = {
    changes.foldLeft[Diff](this)((acc: Diff, change: Change) => acc.withChange(path, change))
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
      case None => new DeepDiff(property.get(target).get, Map())
    }
  }

  override def withChange(property: Property, change: Change): DeepDiff = {
    val newMod = propChanges.get(property) match {
      case Some(mod) => mod.withChange(change)
      case None => new FlatDiff(property.get(target).get, change) // TODO unsafe Option.get ?
    }

    new DeepDiff(target, propChanges + (property -> newMod))
  }

  override def withChange(change: Change): DeepDiff = withChange(Self, change)

  override def transformTarget() = { // FIXME FIXME FIXME quick dirty fixes for sets
    var transformedElems = List[Any]()
    propChanges.foreach({
      case (Self, diff) => {} // self changes must be performed after all nested ones // FIMXE not really :(
      case (prop, diff) => {
        if (target.isInstanceOf[JSet]) {
          val set = target.asInstanceOf[JSet]
          set.remove(diff.target)
          diff.transformTarget()
          transformedElems ::= diff.target
        } else {
          diff.transformTarget()
        }
      }
    })
    propChanges.get(Self) match {
      case Some(diff) => diff.transformTarget()
      case None => {}
    }
    for (elem <- transformedElems) {
      target.asInstanceOf[JSet].add(elem)
    }
  }

  override def hashCode = 13 * target.hashCode + propChanges.hashCode

  override def equals(other: Any) = {
    other match {
      case that: DeepDiff => that.target == target && that.propChanges == propChanges
      case _ => false
    }
  }

  override def forTarget(newTarget: Any) = {
    val newPropChanges = propChanges.get(Self) match {
      case None => propChanges
      case Some(subDiff) => propChanges + (Self -> subDiff.forTarget(newTarget))
    }
    new DeepDiff(newTarget, newPropChanges)
  }

  override def toString = "DeepDiff[" + propChanges.mkString("", ",", "") + "]"
}