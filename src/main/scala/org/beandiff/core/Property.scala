package org.beandiff.core

object Property {
  val IndexPrefix = '['
  val IndexSuffix = ']'
  
  def of(propStr: String): Property = {
    if (!propStr.contains(IndexPrefix))
      new Property(propStr)
    else {
      val name = propStr.substring(0, propStr.indexOf(IndexPrefix))
      val index = propStr.substring(name.length + 1, propStr.length - 1)
      new Property(name, index.toInt)
    }
  }
}

class Property(val name: String, val index: Int) {

  def this(name: String) = {
    this(name, -1)
  }
  
  def canEqual(other: Any) = {
    other.isInstanceOf[org.beandiff.core.Property]
  }
  
  override def equals(other: Any) = {
    other match {
      case that: org.beandiff.core.Property => that.canEqual(Property.this) && name == that.name && index == that.index
      case _ => false
    }
  }
  
  override def hashCode() = {
    val prime = 41
    prime * (prime + name.hashCode) + index.hashCode
  }
  
  override def toString() = {
    name + {
      if (index != -1) "[" + index + "]" else "";
    }
  }
}