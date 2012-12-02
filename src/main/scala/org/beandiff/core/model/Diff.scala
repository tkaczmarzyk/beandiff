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

// TODO factory for values such as EmptyDiff etc
trait Diff extends Change {

  def hasDifference(): Boolean
  def hasDifference(path: String): Boolean
  def hasDifference(p: Path): Boolean
  
  def leafChanges: Iterable[(Path, Change)]
  def changes: Iterable[(Property, Change)]
  
  def withChange(property: Property, change: Change): Diff
  def withChange(path: Path, change: Change): Diff
  
  def transformTarget(): Unit
  def target: Any
}