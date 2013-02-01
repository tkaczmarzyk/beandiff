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

class IgnoreCaseStringEqualityInvestigator extends EqualityInvestigator {

  def areEqual(o1: Any, o2: Any): Boolean = {
    require(o1.isInstanceOf[String])
    require(o2.isInstanceOf[String])
    
    val s1 = o1.asInstanceOf[String]
    val s2 = o2.asInstanceOf[String]
    
    ((s1 == null && s2 == null)
        || (s1 != null && s1.equalsIgnoreCase(s2)))
  }
}