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
package org.beandiff.display

import org.beandiff.core.model.DiffOldImpl
import org.beandiff.core.model.EmptyPath
import org.beandiff.core.model.LeafDiff
import org.beandiff.core.model.Path
import org.beandiff.core.model.NewValue

class PlainTextDiffPresenter(
  private val pathValueSeparator: String = " -- ",
  private val valuesSeparator: String = " vs ",
  private val valueQuote: String = "'",
  private val differenceSeparator: String = "\n") extends DiffPresenter {

  def present(d: DiffOldImpl): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder

      for ((path, change) <- d.changes) {
        result.append(path).append(pathValueSeparator)
        result.append(valueQuote).append(path.value(d.target)).append(valueQuote)
        result.append(valuesSeparator)
        result.append(valueQuote).append(change.newValue).append(valueQuote)
        result.append(differenceSeparator)
      }

      result.toString
    }
  }
}