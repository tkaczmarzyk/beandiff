package org.beandiff.core

object EndOnSimpleTypeStrategy extends DescendingStrategy {

  private val leafClasses = List(classOf[String], classOf[Boolean],
    classOf[Int], classOf[Integer], classOf[Long], classOf[Double])

  def shouldProceed(obj1: Any, ojb2: Any): Boolean = {
    !leafClasses.exists(obj1.getClass == _)
  }
}