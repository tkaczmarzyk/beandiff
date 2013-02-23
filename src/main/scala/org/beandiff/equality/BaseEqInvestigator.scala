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
package org.beandiff.equality

protected abstract class BaseEqInvestigator extends EqualityInvestigator {

  def objsAreEqual(o1: Any, o2: Any): Boolean
  
  override final def areEqual(o1: Any, o2: Any) = {
    if (identical(o1, o2)) true
    else if (o1 == null || o2 == null) false
    else if (o1.getClass != o2.getClass) false
    else objsAreEqual(o1, o2)
  }
  
  private def identical(o1: Any, o2: Any) = {
    if (o1 == null && o2 == null) true
    else if (o1.isInstanceOf[AnyRef] && o2.isInstanceOf[AnyRef]) 
      (o1.asInstanceOf[AnyRef] eq o2.asInstanceOf[AnyRef])
    else if (!o1.isInstanceOf[AnyRef] && !o2.isInstanceOf[AnyRef])
      o1 == o2
    else false
  }
}