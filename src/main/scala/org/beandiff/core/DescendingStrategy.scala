package org.beandiff.core

//TODO rename to sth like EndStrategy
trait DescendingStrategy {

  def shouldProceed(obj1: Any, obj2: Any): Boolean
}