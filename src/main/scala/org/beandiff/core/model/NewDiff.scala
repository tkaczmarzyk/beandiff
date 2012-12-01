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


class NewDiff(
  private val path: Path,
  private val target: Any,
  private val propChanges: Map[Property, Change]) extends Change {

  def this(target: Any, changes: Map[Property, Change]) =
    this(EmptyPath, target, changes)

    
  def changes: Iterable[(Path, Change)] =
    changes(EmptyPath)

  private def changes(currentPath: Path): Iterable[(Path, Change)] = {
    propChanges.toList.flatMap({
      case (prop, change) => change match { //TODO avoid direct type checks
        case diff: NewDiff => diff.changes(currentPath)
        case _ => List((currentPath.step(prop), change))
      }
    })
  }
    
  override def updateTarget() = {
    propChanges.foreach({
      case (prop, change) => change.updateTarget()
    })
  }
}