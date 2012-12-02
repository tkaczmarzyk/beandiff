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
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.model.Path
import org.beandiff.core.model.EmptyPath
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.model.Change

class TransformingDiffEngine(
    private val parent: DiffEngine,
    private val transformer: ObjectTransformer,
    private val translators: Map[Class[_ <: Change], ChangeTranslator]) extends DiffEngine {

  override def calculateDiff(o1: Any, o2: Any): Diff = {
    calculateDiff0(new DiffImpl(EmptyPath, o1, Map()), EmptyPath, o1, o2)
  }
  
  private[core] override def calculateDiff0(zero: Diff, location: Path, o1: Any, o2: Any) = {
    val t1 = transformer.transform(o1)
    val t2 = transformer.transform(o2)
    
    val diff = parent.calculateDiff0(zero, location, t1, t2)
    
    diff.changes.foldLeft(diff)(
        (diff, propChange) => diff.withChange(propChange._1, transform(propChange._2))) //TODO test withChange(emptyDiff)
  }
  
  private def transform(change: Change) = {
    translators.get(change.getClass) match {
      case Some(t) => t.translate(change)
      case None => change
    }
  }
}