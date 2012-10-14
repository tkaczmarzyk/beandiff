package org.beandiff.core

import java.util.List
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import org.beandiff.equality.ReflectiveComparator

class CollectionRoutePlanner extends RoutePlanner {

  private val delegate = new ListRoutePlanner
  private val comparator = new ReflectiveComparator
  
  
  def guide(currentPath: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit = {
    val transformed1 = sortedElements(val1.asInstanceOf[Collection[_]])
    val transformed2 = sortedElements(val2.asInstanceOf[Collection[_]])
    
    delegate.guide(currentPath, transformed1, transformed2, walker)
  }
  
  private def sortedElements(col: Collection[_]): List[_] = {
    if (col.isInstanceOf[List[_]])
      col.asInstanceOf[List[_]]
    else {
      val copy = new ArrayList[Any](col)
      Collections.sort(copy, comparator)
      copy
    }
  }
}