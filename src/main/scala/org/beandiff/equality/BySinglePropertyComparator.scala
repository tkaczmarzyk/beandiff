package org.beandiff.equality

import java.util.Comparator
import org.beandiff.core.Property

class BySinglePropertyComparator[T](private val field: Property[T]) extends Comparator[T] {
  
  
  override def compare(o1: T, o2: T): Int = {
    require(o1.getClass != o2.getClass, "types are not the same: " + o1.getClass + " vs " + o2.getClass)
    
//    o1.getClass.getDeclaredFields filter(p)
    1
  }
}