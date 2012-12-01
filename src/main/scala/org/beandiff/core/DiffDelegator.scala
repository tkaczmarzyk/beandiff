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

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.model.EmptyPath
import org.beandiff.core.model.Path
import org.beandiff.core.model.Diff
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.model.NewValue
import org.beandiff.core.model.Self

class DiffDelegator(
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy) extends DiffEngine {

  private val engines = new ClassDictionary(new LeafDiffEngine(this, eqInvestigators, descStrategy))
  	.withEntry(classOf[java.util.Set[_]] -> new TransformingDiffEngine(this, new ToListTransformer))
  
  
  def calculateDiff(o1: Any, o2: Any): Diff = {
    val engine = if (o1 == null) engines.defaultValue else engines(o1.getClass)
    engine.calculateDiff(o1, o2)
  }

}