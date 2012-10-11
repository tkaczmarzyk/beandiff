package org.beandiff.core

trait DescendingStrategy {

  def shouldProceed(c: Class[_]): Boolean
}