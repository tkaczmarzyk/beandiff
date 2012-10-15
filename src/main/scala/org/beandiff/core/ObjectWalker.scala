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