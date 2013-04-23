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

import java.util.ArrayList
import java.util.Arrays
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Property
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.Change
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.ElementProperty

class TransformingDiffEngine(
  private val delegate: DiffEngineCoordinator,
  private val transformer: ObjectTransformer,
  private val changeTranslators: Map[Class[_ <: Change], ChangeTranslation],
  private val propertyTranslators: Map[Class[_ <: Property], ((Property, Any) => Property)] = Map()) extends DiffEngine { // TODO

  override def calculateDiff(o1: Any, o2: Any): Diff = {
    val t1 = transformer.transform(o1)
    val t2 = transformer.transform(o2)

    val diff = delegate.calculateDiff(Diff(o1), Self, t1, t2) // FIXME in feint test: Diff[[0]-> Diff[Self->Flat....  -- but then unnecessary Diff[Self is removed below

    val result = translateSelfChanges(diff).forTarget(o1)
    
    translateProperties(result)
  }

  private def translateSelfChanges(original: Diff) = { // TODO tests (e.g. transform(Diff[[0] -> FlatChangeSet[NewValue[1->2]]]))
    val selfChanges = original.changes(EmptyPath)

    selfChanges match { // TODO reeduce complexity of this expression
      case None => original
      case Some(changes) =>
        changes.leafChanges.foldLeft(original)((acc, pathChange) => {
          val path = pathChange._1
          val change = pathChange._2
          changeTranslators.get(change.getClass) match {
            case Some(t) => acc.without(path, change).withChanges(path, t.translate(change))
            case None => acc
          }
        })
    }
  }

  private def translateProperties(original: Diff): Diff = {
    original.changes.foldLeft(original)(
        (acc: Diff, propDiff: (Property, Diff)) => {
          propertyTranslators.get(propDiff._1.getClass) match {
            case Some(t) => {
              val oldProp = propDiff._1
              val subdiff = propDiff._2
              val newProp = t(oldProp, subdiff.target)
              acc.without(oldProp).withChanges(newProp, subdiff)
            }
            case None => acc
          }
        })
  }
}