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

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

// TODO reduce mutability
class DiffOldImpl(
    val o1: Any, 
    val o2: Any, 
    val diffs: Map[Property, DiffOldImpl]) { // FIXME improve encapsulation

  
  def this(o1: Any, o2: Any) = this(o1, o2, new HashMap)

  
  def update(p: Path, d: DiffOldImpl): Unit = {
    if (p.depth == 1) {
      diffs += (p.head -> d)
    } else {
      diffs(p.head).update(p.tail, d)
    }
  }
  
  def hasDifference(): Boolean = diffs.exists((x: (Property, DiffOldImpl)) => {x._2.isInstanceOf[LeafDiff] || x._2.hasDifference}) //FIXME remove instanceOf check. Should a sub-Diff be created if no difference on the path?
  
  def hasDifference(path: String): Boolean = hasDifference(Path.of(path))
  
  def hasDifference(p: Path): Boolean = {
    if (p.depth == 0) 
      hasDifference()
    else if (!diffs.contains(p.head))
      false
    else
      diffs(p.head).hasDifference(p.tail)
  }
  
  override def toString() = "Diff[" + o1 + ", " + o2 + "]"
}