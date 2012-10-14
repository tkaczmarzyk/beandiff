package org.beandiff.core

object EndOnSimpleTypeStrategy extends DescendingStrategy {

  private val leafClasses = List(classOf[String], classOf[Boolean],
    classOf[Int], classOf[Integer], classOf[Long], classOf[Double])

  def shouldProceed(obj: Any): Boolean = {
    obj != null && !leafClasses.exists(obj.getClass == _)
  }
}