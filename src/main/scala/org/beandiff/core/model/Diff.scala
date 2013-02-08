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


object Diff {
  
  def apply(target: Any, change: Change, changes: Change*): Diff = {
    val allChanges = change :: changes.toList
    new FlatDiff(target, allChanges)
  }
  
  def apply(target: Any, nestedChanges: Map[Property, Diff]): Diff = {
    new DeepDiff(target, nestedChanges)
  }
  
  def apply(target: Any): Diff = new FlatDiff(target, List[Change]())
}

// TODO factory for values such as EmptyDiff etc
trait Diff {

  def hasDifference(): Boolean
  
  @deprecated("might lead to unexpected result when collection with insertion/deletion changes is on the path")
  def hasDifference(path: String): Boolean
  @deprecated("might lead to unexpected result when collection with insertion/deletion changes is on the path")
  def hasDifference(p: Path): Boolean
  
  def leafChanges: Traversable[(Path, Change)]
  def changes: Traversable[(Property, Diff)] // TODO
  def changes(path: Path): Diff
  
  def withChanges(property: Property, changes: Diff): Diff
  def withChanges(path: Path, changes: Diff): Diff
  
  def withChange(change: Change): Diff // TODO?
  def withChange(path: Path, change: Change): Diff
  def withChange(property: Property, change: Change): Diff
  
  def without(property: Property): Diff
  def without(path: Path): Diff
  
  def transformTarget(): Unit
  def target: Any // TODO remove?
}