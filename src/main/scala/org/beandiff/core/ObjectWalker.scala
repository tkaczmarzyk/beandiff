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
package org.beandiff.core

import org.beandiff.support.ClassDictionary
import ObjectWalker._
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath

object ObjectWalker {
  final val DefaultRoutePlanners: ClassDictionary[RoutePlanner] = new ClassDictionary(new FieldRoutePlanner,
    (classOf[java.util.List[_]], new ListRoutePlanner))
  
  final val DefaultTransformers: ClassDictionary[ObjectTransformer] = new ClassDictionary(NoopTransformer,
    (classOf[java.util.Collection[_]], new ToListTransformer()))
}

// TODO depth-first and breadth-first strategies
// TODO move to support package?
class ObjectWalker(
  val descStrategy: DescendingStrategy,
  val routePlanners: ClassDictionary[RoutePlanner],
  val transformers: ClassDictionary[ObjectTransformer],
  val callback: (Path, Any, Any, Boolean) => Unit) {

  def this(descStrategy: DescendingStrategy, callback: (Path, Any, Any, Boolean) => Unit) = {
    this(descStrategy, DefaultRoutePlanners, DefaultTransformers, callback)
  }

  def walk(o1: Any, o2: Any): Unit = {
    walk(EmptyPath, o1, o2)
  }

  def walk(current: Path, o1: Any, o2: Any): Unit = {
    val isLeaf = !descStrategy.shouldProceed(current, o1, o2)

    val t1 = if (o1 != null) transformers(o1.getClass).transform(o1) else null
    val t2 = if (o2 != null) transformers(o2.getClass).transform(o2) else null
    
    callback(current, t1, t2, isLeaf)
    
    if (!isLeaf) {
      routePlanners(t1.getClass()).guide(current, t1, t2, this)
    }
  }
}