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
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Self
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.model.change.Change

class TransformingDiffEngine(
  private val delegate: DiffEngineCoordinator,
  private val transformer: ObjectTransformer,
  private val translators: Map[Class[_ <: Change], ChangeTranslation]) extends DiffEngine { // TODO

  override def calculateDiff(o1: Any, o2: Any): Diff = {
    val t1 = transformer.transform(o1)
    val t2 = transformer.transform(o2)

    val diff = delegate.calculateDiff(Diff(o1), Self, t1, t2) // FIXME in feint test: Diff[[0]-> Diff[Self->Flat....  -- but then unnecessary Diff[Self is removed below

    val result = translateSelfChanges(diff)

    result.forTarget(o1)
  }

  private def translateSelfChanges(original: Diff) = { // TODO tests (e.g. transform(Diff[[0] -> FlatChangeSet[NewValue[1->2]]]))
    val selfChanges = original.changes(EmptyPath)

    selfChanges match {
      case None => original
      case Some(changes) =>
        changes.leafChanges.foldLeft(original)((acc, pathChange) => {
          val path = pathChange._1
          val change = pathChange._2
          translators.get(change.getClass) match {
            case Some(t) => acc.without(path, change).withChanges(path, t.translate(change))
            case None => acc
          }
        })
    }
  }
}