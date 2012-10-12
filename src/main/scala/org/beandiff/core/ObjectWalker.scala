package org.beandiff.core

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import java.util.ArrayList

class ObjectWalker(val descStrategy: DescendingStrategy, val handlers: PropertyHandlerDictionary) {

  def this() = {
    this(EndOnSimpleTypeStrategy, new PropertyHandlerDictionary(new DefaultPropertyHandler,
      (classOf[List[_]], new ListHandler),
      (classOf[ArrayList[_]], new ListHandler)))
  }

  def walk(o1: Any, o2: Any)(callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    walk(EmptyPath, o1, o2)(callback)
  }

  def walk(current: Path, o1: Any, o2: Any)(callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    val isLeaf = !descStrategy.shouldProceed(o1)

    if (!isLeaf) {
      o1.getClass.getDeclaredFields foreach {
        f =>
          f.setAccessible(true)

          val val1 = f.get(o1)
          val val2 = f.get(o2)
          val path = current.step(new Property(f.getName))

          handlers(val1.getClass()).handle(val1, val2, path, this, callback)
      }
    } else {
      callback(current, o1, o2, isLeaf)
    }
  }
}