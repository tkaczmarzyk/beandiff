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
package org.beandiff.display

import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Path
import org.beandiff.core.model.NewValue
import org.beandiff.core.model._
import org.beandiff.core.model.Diff


class PlainTextDiffPresenter(
  private val pathValueSeparator: String = " -- ",
  private val valuesSeparator: String = " vs ",
  private val valueQuote: String = "'",
  private val differenceSeparator: String = "\n") extends DiffPresenter {

  override def present(d: Diff): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder

      for ((path, change) <- d.leafChanges) { // TODO temporary amendments to the new model
        change match { // TODO better hierarchy for changes // TODO use sealed classes?
          case Deletion(x, index) => {
            result.append(path.withIndex(index)).append(pathValueSeparator).append("deleted")
          }
          
          case Insertion(x, index) => {
            result.append(path.withIndex(index)).append(pathValueSeparator)
            result.append("inserted ").append(valueQuote).append(x).append(valueQuote)
          }
          
          case Addition(x) => {
            result.append(path).append(pathValueSeparator);
            result.append("added ").append(valueQuote).append(x).append(valueQuote)
          }
          
          case Removal(x) => {
            result.append(path).append(pathValueSeparator);
            result.append("removed ").append(valueQuote).append(x).append(valueQuote)
          }
          
          case x => {
            result.append(path).append(pathValueSeparator)
	        result.append(valueQuote).append(change.oldValue).append(valueQuote)
	        result.append(valuesSeparator)
	        result.append(valueQuote).append(change.newValue).append(valueQuote)
          }
        }
        
        result.append(differenceSeparator)
      }

      result.toString
    }
  }
}