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

import java.util.List
import org.beandiff.core.model.Path
import org.beandiff.core.model.IndexProperty

class ListRoutePlanner extends RoutePlanner {

  def guide(current: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit = {
    val list1 = val1.asInstanceOf[List[_]]
    val list2 = val2.asInstanceOf[List[_]]
    
    for (i <- 0 until (list1.size max list2.size)) {
      walker.walk(current.withIndex(i), get(list1, i), get(list2, i))
    }
  }
  
  override def routes(o1: Any, o2: Any) = {
    val list1 = o1.asInstanceOf[List[_]]
    val list2 = o2.asInstanceOf[List[_]]
    
    for (i <- 0 until (list1.size max list2.size)) 
    	yield (new IndexProperty(i), (get(list1, i), get(list2, i)))
  }
  
  private def get(list: List[_], index: Int) =
    if (list.size > index) list.get(index) else null // TODO sth better than null
}