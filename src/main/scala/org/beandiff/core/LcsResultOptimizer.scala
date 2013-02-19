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

// FIXME: an experimental feature, just a prototype -- if it's OK, then refactor (e.g. move some logic to Changeset.optimize?)
class LcsResultOptimizer(
  parent: DiffEngineCoordinator,
  lcsEngine: LcsDiffEngine) extends DiffEngine {

  def calculateDiff(o1: Any, o2: Any) = {
    val diff = lcsEngine.calculateDiff(o1, o2)
    optimizeDiff(diff)
  }

  private def optimizeDiff(diff: Diff): Diff = {
    if (!diff.hasDifference)
      diff
    else
      optimize(diff)
  }
  
  // FIXME temporary, ugly prototype
  private def optimize(diff: Diff): Diff = {
      var result: Diff = Diff(diff.target)

      var skip = List[Change]()

      for {
        (path1, change1) <- diff.leafChanges if change1.isInstanceOf[Deletion]
        (path2, change2) <- diff.leafChanges if change2.isInstanceOf[Insertion] && change1.targetProperty == change2.targetProperty
      } {
        skip :::= List(change1, change2)
        result = parent.calculateDiff(result, change1.targetProperty, change1.oldValue.get, change2.newValue.get)
      }

      for ((path, change) <- diff.leafChanges) {
        if (!skip.contains(change)) {
            result = result.withChange(path, change)
        }
      }

      result
  }
}