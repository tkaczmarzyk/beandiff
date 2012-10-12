package org.beandiff.core

import java.util.List

class ListHandler extends PropertyHandler {

  def handle(val1: Any, val2: Any, current: Path, walker: ObjectWalker, callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    val list1 = val1.asInstanceOf[List[_]]
    val list2 = val2.asInstanceOf[List[_]]
    
    for (i <- 0 until (list1.size max list2.size)) {
      walker.walk(current.withIndex(i), list1.get(i), list2.get(i))(callback)
    }
  }
}