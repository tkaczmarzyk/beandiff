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

import org.beandiff.TypeDefs.JList
import org.beandiff.TypeDefs.JSet
import org.beandiff.core.model.Diff
import org.beandiff.core.model.DescendingHistory
import org.beandiff.core.model.Property
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.Self
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.support.ClassDictionary
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.translation.InsertionToAddition
import org.beandiff.core.translation.DeletionToRemoval
import org.beandiff.lcs.MemoizedLcsCalc
import org.beandiff.equality.DiffEqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.equality.ObjectType
import org.beandiff.equality.Value
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.core.translation.NewValueToRmAdd
import org.beandiff.core.translation.ShiftToNothing
import org.beandiff.core.model.change.Shift

class DelegatingDiffEngine( // TODO responsibility has been extended, consider renaming + separate interface?
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy,
  typeDefs: ClassDictionary[ObjectType] = null) extends DiffEngine with DiffEngineCoordinator {

  private val objTypeDefs =
    if (typeDefs != null)
      if (typeDefs.defaultValue == null)
        typeDefs.withDefault(Value(new DiffEqualityInvestigator(this)))
      else
        typeDefs
    else
      new ClassDictionary[ObjectType](Value(new DiffEqualityInvestigator(this)))

  private val engines = (new ClassDictionary(new LeafDiffEngine(this)))
    .withEntry(classOf[JList] -> new LcsResultOptimizer(this,
      new LcsDiffEngine(this, objTypeDefs, new MemoizedLcsCalc)))
    .withEntry(classOf[JSet] ->
      new TransformingDiffEngine(this, new ToListTransformer,
        Map(classOf[Shift] -> new ShiftToNothing,
          classOf[NewValue] -> new NewValueToRmAdd,
          classOf[Insertion] -> new InsertionToAddition,
          classOf[Deletion] -> new DeletionToRemoval)))

  private var visited = DescendingHistory()

  def calculateDiff(o1: Any, o2: Any): Diff = {
    calculateDiff(Diff(o1), Self, o1, o2)
  }

  def calculateDiff(zero: Diff, location: Property, o1: Any, o2: Any): Diff = {
    if (visited.hasSeen(o1) || !descStrategy.shouldProceed(visited.currentPath.step(location), o1, o2)) {
      if (!getEqInvestigator(o1, o2).areEqual(o1, o2))
        zero.withChange(new NewValue(location, o1, o2))
      else
        zero
    } else {
      visited = visited.step(location, o1)
      val engine = if (o1 == null) engines.defaultValue else engines(o1.getClass)
      val result = engine.calculateDiff(o1, o2)
      visited = visited.stepBack
      zero.withChanges(location, result)
    }
  }

  private def getEqInvestigator(val1: Any, val2: Any) = { // TODO: move null-checks to ClassDictionary ?
    if (val1 == null && val2 == null)
      eqInvestigators.defaultValue
    else {
      val nonNull = if (val1 != null) val1 else val2
      eqInvestigators(nonNull.getClass)
    }
  }
}