package org.beandiff.core

class EndOnNullStrategy(
  private val delegate: DescendingStrategy) extends DescendingStrategy {

  def shouldProceed(o1: Any, o2: Any): Boolean = {
    if (o1 != null && o2 != null)
      delegate.shouldProceed(o1, o2)
    else false
  }
}