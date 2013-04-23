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

import org.beandiff.equality.ObjectType
import org.beandiff.support.ClassDictionary
import org.beandiff.core.model.Diff
import org.beandiff.TypeDefs.JMap
import scala.collection.JavaConversions._
import org.beandiff.core.model.change.KeyRemoval
import org.beandiff.core.model.change.Association
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.Property
import org.beandiff.core.model.KeyProperty
import org.beandiff.core.model.KeyProperty
import org.beandiff.equality.Value
import org.beandiff.equality.Entity
import org.beandiff.core.model.KeyProperty
import org.beandiff.core.model.Self


class MapDiffEngine(
  private val delegate: DiffEngineCoordinator,
  private val objTypes: ClassDictionary[ObjectType]) extends DiffEngine {

  
  def calculateDiff(o1: Any, o2: Any) = {
    val m1 = o1.asInstanceOf[JMap]
    val m2 = o2.asInstanceOf[JMap]
    
    var diff = Diff(o1)
    
    for (entry <- m1.entrySet()) {
      if (!m2.containsKey(entry.getKey)) {
        diff = diff.withChange(KeyRemoval(entry))
      } else {
        val oldVal = m1.get(entry.getKey)
        val newVal = m2.get(entry.getKey)
        
        if (objTypes(oldVal, newVal).allowedToDiff(oldVal, newVal)) {
          diff = delegate.calculateDiff(diff, KeyProperty(entry.getKey()), oldVal, newVal) // TODO duplicated diff calculation when diff eqInvestigator is used: !valuesEqual(entry.getKey)(m1, m2)
        } else {
          diff = diff.withChange(Self, NewValue(KeyProperty(entry.getKey), Some(oldVal), Some(newVal)))
        }
      }
    }
    for (entry <- m2.entrySet()) {
      if (!m1.containsKey(entry.getKey)) {
        diff = diff.withChange(Association(entry))
      }
    }
    
    diff
  }
}