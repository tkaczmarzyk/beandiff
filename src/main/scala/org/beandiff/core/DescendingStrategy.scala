package org.beandiff.core

//TODO rename to sth like EndStrategy
trait DescendingStrategy {

  def shouldProceed(obj: Any): Boolean
}