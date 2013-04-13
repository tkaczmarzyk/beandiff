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
package org.beandiff.lcs

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.ObjectType
import scala.collection.mutable.ListBuffer


class BottomUpLcsCalc extends LcsCalc {

  private val gridGen = new LcsGridGenerator
  
  
  override def lcs(xs: Seq[Any], ys: Seq[Any])(implicit objTypes: ClassDictionary[ObjectType]): Seq[Occurence] = {
    val grid = gridGen.generate(xs, ys)
    val lcs = ListBuffer[Occurence]()

    var (i, j) = (xs.size - 1, ys.size - 1)
    
    while (grid((i, j))._2 != 'none) {
      val move = grid(i, j)._2
      if (move == 'diag) {
        lcs += Occurence(xs(i), i, j)
        i = i - 1
        j = j - 1
      } else if (move == 'down) {
        i = i - 1
      } else if (move == 'right) {
        j = j - 1
      }
    }
    
    lcs.reverse
  }
}