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
import scala.collection.mutable.Map

private[lcs] class LcsGridGenerator {

  /**
   * Generates a grid for LCS calculation.
   *
   * Returns a map where grid[(j, i)] is a pair (n, move) where
   * - n is the length of the LCS of prefixes xs[:i], ys[:j]
   * - move is 'diag, 'down, 'right, or 'none, describing the best move
   *  to (j, i)
   */
  def generate(xs: Seq[Any], ys: Seq[Any])(implicit objTypes: ClassDictionary[ObjectType]): Map[(Int, Int), (Int, Symbol)] = {
    val grid = Map[(Int, Int), (Int, Symbol)]().withDefaultValue((0, 'none))
    
    for {
      (x, i) <- xs.zipWithIndex
      (y, j) <- ys.zipWithIndex
    } {
      if (objTypes(x, y).areEqual(x, y)) {
        val len = grid((i - 1, j - 1))._1 + 1
        grid((i, j)) = (len, 'diag)
      } else {
        val left = grid((i, j - 1))._1
        val over = grid((i - 1, j))._1
        if (left < over)
          grid((i, j)) = (over, 'down)
        else
          grid((i, j)) = (left, 'right)
      }
    }
    
    grid
  }
}