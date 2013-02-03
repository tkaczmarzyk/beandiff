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
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.DelAdd
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Addition
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Removal


// FIXME better name, clearer responsibility, avoid if-else
class IndexPropChangeTranslator extends ChangeTranslator {

  override def translate(change: Change) = {
    if (change.isInstanceOf[NewValue]) {
      new DelAdd(change.oldValue, change.newValue) // TODO verify that property is IndexProperty
    } else if (change.isInstanceOf[Insertion]) {
      new Addition(change.newValue)
    } else if (change.isInstanceOf[Deletion]) {
      new Removal(change.oldValue) 
    } else {
      change
    }
  }
}