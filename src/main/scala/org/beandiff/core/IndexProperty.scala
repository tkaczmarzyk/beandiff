package org.beandiff.core

import java.util.List
import org.beandiff.support.ObjectSupport._

class IndexProperty(val index: Int) extends Property {
  
  override def value(o: Any) = {
    if (o.isInstanceOf[List[_]])
      o(index)
    else null
  }
  
  override def equals(other: Any) = {
    other match {
      case that: IndexProperty => index == that.index
      case _ => false
    }
  }
  
  override def hashCode() = {
    index.hashCode
  }
  
  override def toString() = {
    "[" + index + "]"
  }
}