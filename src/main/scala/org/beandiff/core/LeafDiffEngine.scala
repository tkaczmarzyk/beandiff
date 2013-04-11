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

import org.beandiff.core.model.Diff
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.DeepDiff
import org.beandiff.support.ClassDictionary
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.DeepDiff

private class LeafDiffEngine( // TODO responsibility has been reduced, consider name change
  private val delegate: DiffEngineCoordinator) extends DiffEngine {

  private val routePlanners = ObjectWalker.DefaultRoutePlanners // TODO

  def calculateDiff(o1: Any, o2: Any): Diff = {
    val zero = Diff(o1)
    val routes = routePlanners(o1.getClass).routes(o1, o2)

    routes.foldLeft(zero)(
      (accDiff, route) => route match {
        case (prop, (Some(obj1), Some(obj2))) => delegate.calculateDiff(accDiff, prop, obj1, obj2)
        case (prop, (Some(o), None)) => accDiff.withChange(Self, NewValue(prop, Some(o), None))
        case (prop, (None, Some(o))) => accDiff // TODO
      })
  }

}