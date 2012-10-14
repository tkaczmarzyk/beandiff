package org.beandiff.core

import scala.collection.mutable.HashSet

class BreakCycleStrategy(private val delegate: DescendingStrategy) extends DescendingStrategy {

  private val visitedObjects = new HashSet[Any]
  
  def shouldProceed(o: Any): Boolean = {
    if (delegate.shouldProceed(o) && !visitedObjects.contains(o)) {
      visitedObjects += o
      true
    } else false
  }
}