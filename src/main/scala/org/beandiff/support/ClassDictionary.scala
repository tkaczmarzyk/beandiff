package org.beandiff.support

import org.beandiff.support.ClassSupport._
import scala.collection.mutable.HashMap
import com.sun.org.apache.xerces.internal.jaxp.DefaultValidationErrorHandler

class ClassDictionary[T](val defaultValue: T) {

  private val map = new HashMap[Class[_], T]

  def this(defaultValue: T, values: Iterable[(Class[_], T)]) = {
    this(defaultValue)
    values foreach {
      map += _
    }
  }

  def this(defaultValue: T, values: (Class[_], T)*) = {
    this(defaultValue, values.toList)
  }

  def apply(c: Class[_]): T = {
    if (map.contains(c))
      map(c)
    else {
      val supportedSuperTypes = c.allSuperTypes.filter(map.contains(_))
      if (!supportedSuperTypes.isEmpty)
        map(supportedSuperTypes.head)
      else
        defaultValue
    }
  }
}