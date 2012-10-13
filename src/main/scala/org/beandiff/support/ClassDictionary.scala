package org.beandiff.support

import org.beandiff.support.ClassSupport._
import scala.collection.mutable.HashMap

class ClassDictionary[T](val defaultValue: T) {

  private val map = new HashMap[Class[_], T]

  def this(defaultValue: T, values: (Class[_], T)*) = {
    this(defaultValue)
    values foreach {
      map += _
    }
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