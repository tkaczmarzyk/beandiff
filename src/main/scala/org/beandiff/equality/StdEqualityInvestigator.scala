package org.beandiff.equality

class StdEqualityInvestigator extends EqualityInvestigator {

  def areEqual(obj1: Any, obj2: Any): Boolean = {
    obj1 == obj2
  }
}