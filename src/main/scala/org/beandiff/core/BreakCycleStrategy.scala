/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 * 
 * This file is part of BeanDiff.
 * 
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
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