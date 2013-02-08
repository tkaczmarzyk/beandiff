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
  parent: DiffEngine,
  lcsEngine: LcsDiffEngine) extends DiffEngine {

  def calculateDiff(o1: Any, o2: Any) = {
    calculateDiff0(Diff(o1, Map()), EmptyPath, o1, o2)
  }

  private[core] def calculateDiff0(zero: Diff, location: Path, o1: Any, o2: Any): Diff = {
    val diff = lcsEngine.calculateDiff0(zero, location, o1, o2)

    optimizeDiff(diff, location)
  }

  private def optimizeDiff(diff: Diff, pathToOptimize: Path): Diff = {
    if (!diff.hasDifference(pathToOptimize))
      diff
    else {
      val subdiff = diff.changes(pathToOptimize)
      val optimized = optimize(subdiff.target, subdiff)
      if (optimized.leafChanges.isEmpty) // TODO duplicated somewhere else? move to withChanges method
        diff.without(pathToOptimize)
      else {
        val withoutOldSubdiff = diff.without(pathToOptimize) // TODO clarify the code below (which replaced initial diff.without(pathToOptimize).withChanges(pathToOptimize, optimized) which perhaps could be refactored to handle te tricky scenarios
        optimized.leafChanges.foldLeft(withoutOldSubdiff)(
            (acc: Diff, pathChange: (Path, Change)) => acc.withChange(pathToOptimize ++ pathChange._1, pathChange._2))
      }
    }
  }
  
  // FIXME temporary, ugly prototype
  private def optimize(target: Any, changeset: Diff): Diff = {
    if (!changeset.isInstanceOf[FlatDiff]) { // FIXME
      changeset
    } else {
      var result: Diff = new DeepDiff(target, Map())

      var skip = List[Change]()

      for {
        (path1, change1) <- changeset.leafChanges if change1.isInstanceOf[Deletion]
        (path2, change2) <- changeset.leafChanges if change2.isInstanceOf[Insertion] && change1.asInstanceOf[Deletion].index == change2.asInstanceOf[Insertion].index
      } {
        val index = new IndexProperty(change1.asInstanceOf[Deletion].index)
        skip :::= List(change1, change2)
        result = parent.calculateDiff0(result, Path(index), change1.oldValue, change2.newValue)
      }

      for ((path, change) <- changeset.leafChanges) {
        if (!skip.contains(change)) {
          if (change.isInstanceOf[NewValue]) {
            val newValue = change.asInstanceOf[NewValue]
            result = result.withChanges(path.head, parent.calculateDiff(newValue.oldValue, newValue.newValue))
          } else {
            result = result.withChange(path, change)
          }
        }
      }

      result
    }
  }
}