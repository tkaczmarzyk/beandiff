package org.beandiff.core

class KeyProperty(val key: String) extends Property {

  override def value(o: Any) = {
    throw new IllegalStateException("not implemented yet")
  }
  
  override def equals(other: Any) = {
    other match {
      case that: KeyProperty => key == that.key
      case _ => false
    }
  }
  
  override def hashCode() = {
    key.hashCode
  }
  
  override def toString() = {
    "[" + key + "]"
  }
}