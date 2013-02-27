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

import org.beandiff.core.model.change.Change
import org.beandiff.core.model.Diff
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.Diff
import org.beandiff.core.model.DeepDiff
import org.beandiff.core.model.FlatDiff
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Shift
import org.beandiff.core.model.change.ChangeOrdering
import org.beandiff.core.model.PathChangeOrdering
import org.beandiff.equality.Entity
import org.beandiff.equality.Entity
import org.beandiff.equality.Value
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.IndexProperty

class LcsResultOptimizer(
  parent: DiffEngineCoordinator,
  lcsEngine: LcsDiffEngine) extends DiffEngine {


  def calculateDiff(o1: Any, o2: Any) = {
    val diff = lcsEngine.calculateDiff(o1, o2)
    optimize(diff)
  }

  private def optimize(diff: Diff): Diff = {
    var result = diff

    var skip = List[Change]()

    for {
      (path, change1) <- diff.leafChanges.sorted(PathChangeOrdering)
      (path, change2) <- diff.leafChanges.sorted(PathChangeOrdering)
    } {
      if (!(skip.contains(change1) || skip.contains(change2))) {
        (change1, change2) match {
          case (Deletion(x, idx), Insertion(y, idx2)) if idx == idx2 => { // TODO high complexity, factor out some stuff
            result = result.without(path, change1).without(path, change2)
            skip = change1 :: change2 :: skip
            
            if (lcsEngine.objTypes(x.getClass).allowedToDiff(x, y)) { // FIXME what if y is entity here?
              result = parent.calculateDiff(result, change1.targetProperty, change1.oldValue.get, change2.newValue.get)
            } else {
              result = result.withChange(path, NewValue(IndexProperty(idx), x, y))
            }
          }
          case (Deletion(x, idx), Insertion(y, idx2)) if lcsEngine.objTypes(x.getClass).areEqual(x, y) => {
            result = result.without(path, change1).without(path, change2) // TODO add without(path, changes*)
            result = result.withChange(path, Shift(x, idx, idx2))
            result = parent.calculateDiff(result, change1.targetProperty, x, y)
            skip = change1 :: change2 :: skip
          }
          case _ => {}
        }
      }
    }

    result
  }

}