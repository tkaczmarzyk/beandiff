package org.beandiff.core

import scala.collection.mutable.HashSet
import java.util.IdentityHashMap
import com.google.common.collect.Sets

class BreakCycleStrategy(private val delegate: DescendingStrategy) extends DescendingStrategy {

  private val visitedObjects = Sets.newSetFromMap(new IdentityHashMap[Any, java.lang.Boolean])
  
  def shouldProceed(o1: Any, o2: Any): Boolean = {
    if (delegate.shouldProceed(o1, o2) && !visitedObjects.contains(o1)) {
      if (o1 != null) {
        visitedObjects.add(o1)
      }
      true
    } else false
  }
}