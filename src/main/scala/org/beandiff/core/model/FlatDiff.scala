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
import org.beandiff.core.model.change.NewValue
import scala.collection.immutable.SortedSet
import org.beandiff.core.model.change.ChangeOrdering

private[model] class FlatDiff(
  val target: Any, // TODO decide whether it should be prese, 
  private val selfChanges: List[Change]) extends Diff {

  def this(target: Any, changes: Change*) { // TODO Change parameter to ensure at least 1 change is provided
    this(target, List(changes: _*))
  }

  override def leafChanges = selfChanges.map(ch => (Path(Self), ch))

  override def withChange(change: Change) = withChange(Self, change)

  override def withChange(path: Path, change: Change): Diff = {
    if (path.depth <= 1)
      withChange(path.head, change)
    else
      toDiff.withChange(path, change)
  }

  override def hasDifference(pathToFind: Path): Boolean = {
    if (pathToFind == EmptyPath)
      !selfChanges.isEmpty
    else
      (pathToFind.depth == 1) && selfChanges.exists(_.targetProperty == pathToFind.head)
  }

  override def changes(path: Path): Option[Diff] = {
    if (path != EmptyPath)
      None
    else
      Some(this)
  }

  override def withChanges(path: Path, changes: Seq[Change]) = { // FXIME implementation is duplicated in DeepDiff
    changes.foldLeft[Diff](this)((acc: Diff, change: Change) => acc.withChange(path, change))
  }
  
  override def withChanges(path: Path, changes: Diff): Diff = {
    if (path.depth <= 1)
      withChanges(path.head, changes)
    else
      toDiff.withChanges(path, changes)
  }

  override def without(prop: Property) = {
    if (prop == Self)
      Diff(target)
    else {
      val remainingChanges = selfChanges.filter(_.targetProperty != prop)
      new FlatDiff(target, remainingChanges)
    }
  }
  
  override def without(path: Path) = {
    if (path == EmptyPath)
      new FlatDiff(target) // TODO some kind of inconsistency with DeepDiff (or...?)
    else if (path.depth == 1)
      without(path.head)
    else
      this
  }

  override def without(path: Path, change: Change): Diff = {
    if (path != EmptyPath)
      this
    else
      new FlatDiff(target, selfChanges.filter(_ != change)) // TODO perf
  }
  
  override def withChange(prop: Property, change: Change) = {
    if (prop == Self)
      new FlatDiff(target, change :: selfChanges)
    else
      toDiff.withChange(prop, change)
  }

  override def withChanges(prop: Property, diff: Diff) = { // TODO simplify :( // FIXME it's now based on some assumptions (e.g. that Self cannot be mapped to anything but FlatDiff)
    if (prop == Self) {
      if (selfChanges.isEmpty)
        diff
      else
        diff.leafChanges.foldLeft[Diff](this)(
            (acc: Diff, pathChange: (Path, Change)) => acc.withChange(pathChange._1, pathChange._2))
    } else if (!diff.hasDifference) {
      this
    } else {
      toDiff.withChanges(prop, diff)
    }
  }

  override def hasDifference = !selfChanges.isEmpty

  override def hasDifference(pathStr: String) = toDiff.hasDifference(pathStr)

  def toDiff =
    if (selfChanges.isEmpty)
      new DeepDiff(target, Map())
    else
      Diff(target, Map[Property, Diff](Self -> this))

  override def toString() = "FlatDiff[" + selfChanges.mkString("", ", ", "") + "]"

  override def changes = toDiff.changes

  override def hashCode = 13 * target.hashCode + selfChanges.hashCode
  
  override def equals(other: Any) = {
    other match {
      case that: FlatDiff => target == that.target && selfChanges == that.selfChanges
      case _ => false
    }
  }
  
  override def forTarget(newTarget: Any) = new FlatDiff(newTarget, selfChanges)
  
  override def transformTarget() = {
    for (change <- selfChanges.sorted(ChangeOrdering)) {
      change.perform(target)
    }
  }
}