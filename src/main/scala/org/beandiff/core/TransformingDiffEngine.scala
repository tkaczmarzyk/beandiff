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
import org.beandiff.core.model.Property
import org.beandiff.core.model.DeepDiff
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Self
import org.beandiff.core.model.FlatDiff
import org.beandiff.core.model.FieldProperty


class TransformingDiffEngine(
  private val delegate: DiffEngine,
  private val transformer: ObjectTransformer,
  private val translators: Map[Class[_ <: Change], ChangeTranslation]) extends DiffEngine { // TODO

  override def calculateDiff(o1: Any, o2: Any): Diff = {
    val zero = Diff(o1)
    val t1 = transformer.transform(o1)
    val t2 = transformer.transform(o2)

    val diff = delegate.calculateDiff(t1, t2) // FIXME in feint test: Diff[[0]-> Diff[Self->Flat....  -- but then unnecessary Diff[Self is removed below

    val result = diff.changes.foldLeft(zero)( // TODO tests
      (diff, propChanges) => {
        val transformedChangeset = transform(propChanges._2)
        diff.withChanges(propChanges._1, transformedChangeset)
      })

    //    transformedProperty.yieldTransformed = false // FIXME if set to false, then unable to transform changes from outer collection

    result.forTarget(o1)
  }

  private def transform(original: Diff) = { // TODO tests (e.g. transform(Diff[[0] -> FlatChangeSet[NewValue[1->2]]]))
    val leafChanges = original.leafChanges

    leafChanges.foldLeft(original)( // TODO check 
      (acc: Diff, pathChange: (Path, Change)) => {
        val path = pathChange._1
        val change = pathChange._2
        translators.get(change.getClass) match {
          case Some(t) => acc.without(path, change).withChange(t.translate(change))
          case None => acc
        }
      }) // FIXME FIXME FIXME breaks when flatchangeset becomes a Diff (Diff(self -> diff(...))). Add tests & fix 
  }
}