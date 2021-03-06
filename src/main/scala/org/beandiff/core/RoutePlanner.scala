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

import org.beandiff.core.model.Path
import org.beandiff.core.model.Property
import org.beandiff.core.RoutePlanner.Route

object RoutePlanner {
  type Route = (Property, (Option[Any], Option[Any])) // TODO generalize
  
  def Route[T, S](prop: Property, o1: Option[T], o2: Option[S]): Route = {
    (prop, (o1, o2))
  }
}

trait RoutePlanner {
  
  @deprecated
  def guide(current: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit
  
  def routes(o1: Any, o2: Any): Iterable[Route]
}