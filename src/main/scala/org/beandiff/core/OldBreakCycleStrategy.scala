/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
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

import java.util.ArrayList
import java.util.Arrays
import java.util.IdentityHashMap
import java.util.List

import scala.collection.JavaConversions.asScalaBuffer

import org.beandiff.core.model.Path

@deprecated
class OldBreakCycleStrategy(private val delegate: DescendingStrategy) extends DescendingStrategy {

  private val visitedObjects = new IdentityHashMap[Any, List[Path]]

  def shouldProceed(path: Path, o1: Any, o2: Any): Boolean = {
    if (delegate.shouldProceed(path, o1, o2)) {
      if (!visitedObjects.containsKey(o1)) {
        if (o1 != null) {
          visitedObjects.put(o1, new ArrayList(Arrays.asList(path)))
        }
        true
      } else
        !visitedObjects.get(o1).exists(_.isPrefixOf(path))
    } else false
  }
}