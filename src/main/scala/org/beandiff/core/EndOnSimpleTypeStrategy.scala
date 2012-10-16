package org.beandiff.core

object EndOnSimpleTypeStrategy extends DescendingStrategy {

  private val leafClasses = List(classOf[String], classOf[Boolean],
    classOf[Int], classOf[Integer], classOf[Long], classOf[Double], classOf[Character],
    classOf[java.lang.Long], classOf[java.lang.Float], classOf[java.lang.Double],
    classOf[java.lang.Boolean], classOf[java.lang.Enum[_]])

  def shouldProceed(obj1: Any, ojb2: Any): Boolean = {
    !leafClasses.exists(_.isAssignableFrom(obj1.getClass))
  }
}