package org.beandiff.core

trait DescendingStrategy {

  def shouldProceed(obj: Any): Boolean
}