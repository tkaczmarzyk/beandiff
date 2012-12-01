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

import org.beandiff.core.model.Diff
import org.beandiff.core.model.NewValue
import org.beandiff.core.model.EmptyPath
import org.beandiff.core.model.DiffNewImpl
import org.beandiff.support.ClassDictionary
import org.beandiff.core.model.Path
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.model.Self

private class LeafDiffEngine(
  private val parent: DiffEngine,
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy) extends DiffEngine {

  private val routePlanners = ObjectWalker.DefaultRoutePlanners // TODO

  
  def calculateDiff(o1: Any, o2: Any): DiffNewImpl = {
    def calculateDiff0(currentPath: Path): DiffNewImpl = {
      if (!descStrategy.shouldProceed(o1, o2)) {
        if (!getEqInvestigator(o1, o2).areEqual(o1, o2)) {
          new DiffNewImpl(currentPath, o1, Map(new Self -> new NewValue(o2)))
        } else {
          new DiffNewImpl(currentPath, o1, Map())
        }
      } else {
        val routes = routePlanners(o1.getClass).routes(o1, o2)

        val diffs = routes.map({
          case (prop, (obj1, obj2)) => (prop, parent.calculateDiff(obj1, obj2))
        })

        diffs.foldLeft(new DiffNewImpl(currentPath, o1, Map()))(
          (acc, propDiff) =>
            if (propDiff._2.hasDifference)
              acc.withSubDiff(propDiff._1, propDiff._2)
            else acc
        )
      }
    }

    calculateDiff0(EmptyPath)
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