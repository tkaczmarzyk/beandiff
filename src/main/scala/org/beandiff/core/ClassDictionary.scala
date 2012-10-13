package org.beandiff.core

import scala.collection.mutable.HashMap

class ClassDictionary[T](val defaultValue: T) {

  private val map = new HashMap[Class[_], T]
  
  def this(defaultValue: T, handlers: (Class[_], T)*) = {
    this(defaultValue)
    handlers.foreach(map += _)
  }
  
  def apply(c: Class[_]): T = {
    if (map.contains(c))
      map(c)
    else if (c != classOf[Object])
      apply(c.getSuperclass())
    else
      defaultValue
  }
}