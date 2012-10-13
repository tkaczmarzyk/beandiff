package org.beandiff.support

import java.lang.reflect.Field


object ObjectSupport {
  
  implicit def convert(o: Object) = new ObjectSupport(o)
}

class ObjectSupport(val target: Object) {
  
  def hasField(name: String) = {
    target.getClass.getDeclaredFields().contains((f: Field) => f.getName == name)
  }
  
  def getFieldVal(name: String) = {
    val f = target.getClass.getDeclaredField(name)
    f.setAccessible(true)
    f.get(target)
  }
  
  def apply(index: Int) = target.asInstanceOf[java.util.List[_]].get(index).asInstanceOf[Object]
}