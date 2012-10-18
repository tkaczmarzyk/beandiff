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
package org.beandiff.core

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator

class DiffEngine(
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy) {

  def calculateDiff(o1: Any, o2: Any) = {
    var d = new Diff(o1, o2) //FIXME nicer solution

    new ObjectWalker(new EndOnNullStrategy(descStrategy), // FIXME reduce if/else complexity. // TODO should Diff be created for paths withoudt difference?
      (path, val1, val2, isLeaf) =>
        if (path.depth == 0 && isLeaf && !getEqInvestigator(val1, val2).areEqual(val1, val2))
          d = new LeafDiff(o1, o2)
        else if ((path.depth != 0)) {
          d(path) = 
            if (isLeaf && !getEqInvestigator(val1, val2).areEqual(val1, val2)) new LeafDiff(val1, val2)
            else new Diff(val1, val2)
        }
    ).walk(o1, o2)

    d
  }

  private def getEqInvestigator(val1: Any, val2: Any) = {
    if (val1 == null && val2 == null)
      eqInvestigators.defaultValue
    else {
      val nonNull = if (val1 != null) val1 else val2
      eqInvestigators(nonNull.getClass)
    }
  }
}