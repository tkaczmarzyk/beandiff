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
package org.beandiff.display

import org.beandiff.core.Diff
import org.beandiff.core.EmptyPath
import org.beandiff.core.LeafDiff
import org.beandiff.core.Path

class PlainTextDiffPresenter(
    private val pathValueSeparator: String = " -- ",
    private val valuesSeparator: String = " vs ",
    private val valueQuote: String = "'",
    private val differenceSeparator: String = "\n") extends DiffPresenter {

  
  def present(d: Diff): String = present(EmptyPath, d)
  
  private def present(currentPath: Path, d: Diff): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder
      
      for ((property, diff) <- d.diffs) {
        if (diff.isInstanceOf[LeafDiff]) { //FIXME avoid direct type-checking!
          result.append(currentPath.step(property)).append(pathValueSeparator)
          result.append(valueQuote).append(diff.o1).append(valueQuote)
          result.append(valuesSeparator)
          result.append(valueQuote).append(diff.o2).append(valueQuote)
          result.append(differenceSeparator)
        } else {
          result.append(present(currentPath.step(property), diff))
        }
      }
      
      result.toString
    }
  }
}