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
  private val path: Path, 
  val target: Any, 
  private val propChanges: Map[Property, Change]) extends Diff {

  def this(target: Any, changes: Map[Property, Change]) =
    this(EmptyPath, target, changes)

    
  override def leafChanges: Iterable[(Path, Change)] =
    leafChanges(EmptyPath)

  private def leafChanges(currentPath: Path): Iterable[(Path, Change)] = { // TODO generic method for traversation (with break option)
    propChanges.toList.flatMap({
      case (prop, change) => change match { //TODO avoid direct type checks
        case diff: DiffImpl => diff.leafChanges(currentPath.step(prop))
        case _ => List((currentPath.step(prop), change))
      }
    })
  }
  
  override def changes: Iterable[(Property, Change)] = 
    propChanges.toList
  
  override def withChange(property: Property, change: Change): DiffImpl = {
    new DiffImpl(path, target, propChanges + (property -> change))
  }
  
  override def withChange(path: Path, change: Change): Diff = {
    if (path == null || path.depth == 0) {
      withChange(new Self, change)
    } else if (path.depth == 1) {
      withChange(path.head, change)
    } else {
      val interDiff =
        if (propChanges.contains(path.head))
          propChanges(path.head).asInstanceOf[Diff] //TODO
        else new DiffImpl(this.path.step(path.head), null, Map())
      
      withChange(path.head, interDiff.withChange(path.tail, change))
    }
  }
  
  override def hasDifference(): Boolean =
    !propChanges.isEmpty
    
  override def hasDifference(pathStr: String): Boolean =
    hasDifference(Path.of(pathStr))
  
  override def hasDifference(pathToFind: Path): Boolean = { // FIXME tmp, refactor!!
    if (pathToFind == null || pathToFind == EmptyPath) {
      return hasDifference
    }
    if (pathToFind.depth == 1) {
      propChanges.contains(pathToFind.head)
    } else {
      if (propChanges.contains(pathToFind.head)) {
        val change = propChanges(pathToFind.head)
        if (change.isInstanceOf[Diff]) {
          change.asInstanceOf[Diff].hasDifference(pathToFind.tail)
        } else {
          pathToFind.tail == null || pathToFind.tail == EmptyPath
        }
      } else false
    }
  }
  
  override def transformTarget() = perform(null, null)
  
  override def newValue() = throw new UnsupportedOperationException("tmp")
  override def oldValue() = throw new UnsupportedOperationException("tmp")
    
  override def perform(parent: Any) = {
    propChanges.foreach({
      case (prop, change) => change.perform(target) 
    })
  }
}