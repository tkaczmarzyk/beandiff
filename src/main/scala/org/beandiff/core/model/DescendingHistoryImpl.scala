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
package org.beandiff.core.model


private class DescendingHistoryImpl(
    override val currentPath: Path,
    private val historyReversed: List[Any] = List()) extends DescendingHistory {

  
  override def step(prop: Property, elem: Any) = new DescendingHistoryImpl(currentPath.step(prop), elem :: historyReversed)
  
  override def stepBack = new DescendingHistoryImpl(currentPath.stepBack, historyReversed.tail)
  
  override def hasSeen(elem: Any) = {
    val isRef = elem.isInstanceOf[AnyRef]
    historyReversed.exists((o: Any) =>
      if (isRef) o.isInstanceOf[AnyRef] && (o.asInstanceOf[AnyRef] eq elem.asInstanceOf[AnyRef])
      else elem == o)
  }
}