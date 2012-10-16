/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 * 
 * This file is part of BeanDiff.
 * 
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
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
package org.beandiff.core

import org.beandiff.support.ClassDictionary
import ObjectWalker._

object ObjectWalker {
  final val DefaultRoutePlanners: ClassDictionary[RoutePlanner] = new ClassDictionary(new FieldRoutePlanner,
    (classOf[java.util.Collection[_]], new CollectionRoutePlanner))
}

// TODO depth-first and breadth-first strategies
// TODO move to support package?
class ObjectWalker(
  val descStrategy: DescendingStrategy,
  val routePlanners: ClassDictionary[RoutePlanner],
  val callback: (Path, Any, Any, Boolean) => Unit) {

  def this(descStrategy: DescendingStrategy, callback: (Path, Any, Any, Boolean) => Unit) = {
    this(descStrategy, DefaultRoutePlanners, callback)
  }

  def walk(o1: Any, o2: Any): Unit = {
    walk(EmptyPath, o1, o2)
  }

  def walk(current: Path, o1: Any, o2: Any): Unit = {
    val isLeaf = !descStrategy.shouldProceed(o1, o2)

    if (!isLeaf) {
      routePlanners(o1.getClass()).guide(current, o1, o2, this)
    } else {
      callback(current, o1, o2, isLeaf)
    }
  }
}