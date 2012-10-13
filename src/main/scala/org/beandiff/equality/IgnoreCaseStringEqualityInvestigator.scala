package org.beandiff.equality

class IgnoreCaseStringEqualityInvestigator extends EqualityInvestigator {

  def areEqual(o1: Any, o2: Any): Boolean = {
    require(o1.isInstanceOf[String])
    require(o2.isInstanceOf[String])
    
    val s1 = o1.asInstanceOf[String]
    val s2 = o2.asInstanceOf[String]
    
    ((s1 == null && s2 == null)
        || (s1 != null && s1.equalsIgnoreCase(s2)))
  }
}