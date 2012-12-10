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
import org.beandiff.core.model.DiffImpl
import org.beandiff.support.ClassDictionary
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.model.Self
import org.beandiff.core.model.NewValue
import org.beandiff.core.model.DiffImpl

private class LeafDiffEngine(
  private val delegate: DiffEngine,
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy) extends DiffEngine {

  private val routePlanners = ObjectWalker.DefaultRoutePlanners // TODO

  def calculateDiff(o1: Any, o2: Any): Diff = {
    val zero = new DiffImpl(EmptyPath, o1, Map())
    calculateDiff0(zero, EmptyPath, o1, o2)
  }

  private[core] override def calculateDiff0(zero: Diff, location: Path, o1: Any, o2: Any): Diff = {
    if (!descStrategy.shouldProceed(location, o1, o2)) {
      if (!getEqInvestigator(o1, o2).areEqual(o1, o2)) {
        zero.withChange(location, new NewValue(location.last, o1, o2))
      } else {
        zero
      }
    } else {
      val routes = routePlanners(o1.getClass).routes(o1, o2)

      routes.foldLeft(zero)(
        (accDiff, route) => route match {
          case (prop, (obj1, obj2)) => delegate.calculateDiff0(accDiff, location.step(prop), obj1, obj2)
        })
    }
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