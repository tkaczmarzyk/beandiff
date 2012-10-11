package org.beandiff.equality

class StdEqualityChecker extends EqualityChecker {

  def areEqual(obj1: Object, obj2: Object): Boolean = {
    obj1 == obj2
  }
}