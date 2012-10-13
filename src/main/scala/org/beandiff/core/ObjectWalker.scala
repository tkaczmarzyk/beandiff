package org.beandiff.core

import org.beandiff.support.ClassDictionary

class ObjectWalker(
    val callback: (Path, Any, Any, Boolean) => Unit, 
    val descStrategy: DescendingStrategy,
    val handlers: ClassDictionary[RoutePlanner]) {

  def this(callback: (Path, Any, Any, Boolean) => Unit) = {
    this(callback, EndOnSimpleTypeStrategy, new ClassDictionary(new FieldRoutePlanner,
      (classOf[java.util.List[_]], new ListRoutePlanner)))
  }

  def walk(o1: Any, o2: Any): Unit = {
    walk(EmptyPath, o1, o2)
  }

  def walk(current: Path, o1: Any, o2: Any): Unit = {
    val isLeaf = !descStrategy.shouldProceed(o1)

    if (!isLeaf) {
      handlers(o1.getClass()).handle(current, o1, o2, this)
    } else {
      callback(current, o1, o2, isLeaf)
    }
  }
}