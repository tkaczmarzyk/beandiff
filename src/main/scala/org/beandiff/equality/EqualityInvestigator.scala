package org.beandiff.equality

trait EqualityInvestigator {

  def areEqual(obj1: Any, obj2: Any): Boolean
}