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

import java.util.List
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import org.beandiff.equality.ReflectiveComparator

class CollectionRoutePlanner extends RoutePlanner {

  private val delegate = new ListRoutePlanner
  private val comparator = new ReflectiveComparator
  
  
  def guide(currentPath: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit = {
    val transformed1 = sortedElements(val1.asInstanceOf[Collection[_]])
    val transformed2 = sortedElements(val2.asInstanceOf[Collection[_]])
    
    delegate.guide(currentPath, transformed1, transformed2, walker)
  }
  
  private def sortedElements(col: Collection[_]): List[_] = {
    if (col.isInstanceOf[List[_]])
      col.asInstanceOf[List[_]]
    else {
      val copy = new ArrayList[Any](col)
      Collections.sort(copy, comparator)
      copy
    }
  }
}