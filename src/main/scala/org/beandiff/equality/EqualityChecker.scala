package org.beandiff.equality

trait EqualityChecker {

  def areEqual(obj1: Object, obj2: Object): Boolean
}