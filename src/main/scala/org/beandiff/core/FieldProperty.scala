package org.beandiff.core

import org.beandiff.support.ObjectSupport._

class FieldProperty(val name: String) extends Property {

  override def value(o: Any): Any = {
    if (o hasField name)
      o getFieldVal name
    else null
  }
  
  override def equals(other: Any) = {
    other match {
      case that: FieldProperty => name == that.name
      case _ => false
    }
  }
  
  override def hashCode() = {
    name.hashCode
  }
  
  override def toString() = {
    name
  }
}