package org.beandiff.core

import java.lang.reflect.Field

class Property[T](private val field: Field) {

  require(field.getDeclaringClass.isInstanceOf[T])
  field.setAccessible(true)

  private val owner: Class[T] = field.getDeclaringClass.asInstanceOf[Class[T]]

  
  def this(clazz: Class[T], fieldName: String) = {
    this(clazz.getDeclaredField(fieldName))
  }

  def value(obj: Object) = {
    require(obj.isInstanceOf[T])
    field.get(obj)
  }
}