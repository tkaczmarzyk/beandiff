package org.beandiff.core

class ObjectWalker(val descStrategy: DescendingStrategy, val handlers: ClassDictionary[PropertyHandler]) {

  def this() = {
    this(EndOnSimpleTypeStrategy, new ClassDictionary(new DefaultPropertyHandler,
      (classOf[java.util.AbstractList[_]], new ListHandler)))
  }

  def walk(o1: Any, o2: Any)(callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    walk(EmptyPath, o1, o2)(callback)
  }

  def walk(current: Path, o1: Any, o2: Any)(callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    val isLeaf = !descStrategy.shouldProceed(o1)

    if (!isLeaf) {
      handlers(o1.getClass()).handle(o1, o2, current, this, callback)
    } else {
      callback(current, o1, o2, isLeaf)
    }
  }
}