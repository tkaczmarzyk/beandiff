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

import org.beandiff.core.model.DescendingHistory
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Property
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Shift
import org.beandiff.core.translation.DeletionToRemoval
import org.beandiff.core.translation.InsertionToAddition
import org.beandiff.core.translation.NewValueToRmAdd
import org.beandiff.core.translation.ShiftToNothing
import org.beandiff.equality.DiffEqualityInvestigator
import org.beandiff.equality.Entity
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.ObjectType
import org.beandiff.equality.Value
import org.beandiff.lcs.BottomUpLcsCalc
import org.beandiff.support.ClassDictionary
import org.beandiff.TypeDefs._
import org.beandiff.core.model.IndexProperty
import org.beandiff.core.model.ElementProperty


class DelegatingDiffEngine( // TODO responsibility has been extended, consider renaming + separate interface?
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  descengingStrategy: DescendingStrategy,
  typeDefs: ClassDictionary[ObjectType] = null,
  filter: Filter = AcceptEverything) extends DiffEngine with DiffEngineCoordinator {

  private val objTypeDefs =
    if (typeDefs != null)
      if (typeDefs.defaultValue == null)
        typeDefs.withDefault(Value(new DiffEqualityInvestigator(this)))
      else
        typeDefs
    else
      new ClassDictionary[ObjectType](Value(new DiffEqualityInvestigator(this)))

  private val endOnEntity = new DescendingStrategy {
    override def shouldProceed(path: Path, o1: Any, o2: Any) = {
      objTypeDefs(o1, o2).allowedToDiff(o1, o2)
    }
  }
      
  private val descStrategy = CompositeDescendingStrategy.allOf(descengingStrategy, endOnEntity)
      
  private val engines = (new ClassDictionary(new LeafDiffEngine(this)))
  	.withEntry(classOf[JMap] -> new MapDiffEngine(this, objTypeDefs))
    .withEntry(classOf[JList] -> new LcsResultOptimizer(this,
      new LcsDiffEngine(this, objTypeDefs, new BottomUpLcsCalc)))
    .withEntry(classOf[JSet] ->
      new TransformingDiffEngine(this, new ToListTransformer,
        changeTranslators = Map(classOf[Shift] -> new ShiftToNothing,
          classOf[NewValue] -> new NewValueToRmAdd,
          classOf[Insertion] -> new InsertionToAddition,
          classOf[Deletion] -> new DeletionToRemoval),
        propertyTranslators = Map(
          classOf[IndexProperty] -> ((prop, target) => ElementProperty(target)))))

  private var visited = DescendingHistory()

  def calculateDiff(o1: Any, o2: Any): Diff = {
    calculateDiff(Diff(o1), Self, o1, o2)
  }

  def calculateDiff(zero: Diff, location: Property, o1: Any, o2: Any): Diff = {
    val newLocation = visited.currentPath.step(location)
    
    if (filter.shouldSkip(newLocation, o1, o2)) {
      zero
    } else if (visited.hasSeen(o1) || !descStrategy.shouldProceed(newLocation, o1, o2)) {
      if (!eqInvestigators(o1, o2).areEqual(o1, o2))
        zero.withChange(new NewValue(location, o1, o2))
      else
        zero
    } else {
      visited = visited.step(location, o1)
      val engine = engines(o1, o2)
      val result = engine.calculateDiff(o1, o2)
      visited = visited.stepBack
      zero.withChanges(location, result)
    }
  }
}