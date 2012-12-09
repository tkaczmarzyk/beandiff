/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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
package org.beandiff.support

import scala.collection.JavaConversions.asScalaBuffer

import org.beandiff.TypeDefs.JList


object CollectionSupport {
  implicit def convert[T](xs: Seq[T]) = new CollectionSupport(xs)
  implicit def convert[T](xs: JList) = new CollectionSupport(xs)
}

class CollectionSupport[T](
    private val xs: Seq[T]) {

  def dropIndices(indices: Seq[Int]): Seq[(T, Int)] = {
    xs.zipWithIndex.filter(p => !indices.contains(p._2))
  }
}