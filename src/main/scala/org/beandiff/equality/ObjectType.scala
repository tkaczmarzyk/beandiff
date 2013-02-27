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

sealed trait ObjectType { // TODO move to core?
  def areEqual(o1: Any, o2: Any): Boolean
  
  def allowedToDiff(o1: Any, o2: Any): Boolean
}

case class Entity(idDef: EqualityInvestigator) extends ObjectType {
  override def areEqual(o1: Any, o2: Any) = idDef.areEqual(o1, o2)
  override def allowedToDiff(o1: Any, o2: Any) = idDef.areEqual(o1, o2) 
}
  
case class Value(eqDef: EqualityInvestigator) extends ObjectType {
  override def areEqual(o1: Any, o2: Any) = eqDef.areEqual(o1, o2)
  override def allowedToDiff(o1: Any, o2: Any) = true
}