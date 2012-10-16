package org.beandiff.core

import java.util.List

class ListRoutePlanner extends RoutePlanner {

  def guide(current: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit = {
    val list1 = val1.asInstanceOf[List[_]]
    val list2 = val2.asInstanceOf[List[_]]
    
    for (i <- 0 until (list1.size max list2.size)) {
      walker.walk(current.withIndex(i), get(list1, i), get(list2, i))
    }
  }
  
  private def get(list: List[_], index: Int) =
    if (list.size > index) list.get(index) else null
}