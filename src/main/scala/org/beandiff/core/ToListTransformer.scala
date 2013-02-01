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

import java.util.Collections
import java.util.Collection
import java.util.List
import org.beandiff.equality.ReflectiveComparator
import java.util.ArrayList
import java.util.Comparator

class ToListTransformer(private val comparator: Comparator[Any]) extends ObjectTransformer {

  def this() = {
    this(new ReflectiveComparator)
  }

  def transform(src: Any) = {
    if (src.isInstanceOf[List[_]])
      src
    else {
      val copy = new ArrayList[Any](src.asInstanceOf[Collection[_]])
      Collections.sort(copy, comparator)
      copy
    }
  }
}