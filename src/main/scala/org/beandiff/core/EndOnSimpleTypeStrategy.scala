package org.beandiff.core

object EndOnSimpleTypeStrategy extends DescendingStrategy {

  private val leafClasses = List(classOf[String], classOf[Boolean], classOf[Int], classOf[Integer], classOf[Long], classOf[Double])
  
  def shouldProceed(c: Class[_]): Boolean = {
    !leafClasses.exists(c == _)
  }
}