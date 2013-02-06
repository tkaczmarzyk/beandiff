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
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.translation.ChangeTranslation
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.ChangeSet
import org.beandiff.core.model.Self
import org.beandiff.core.model.FlatChangeSet
import org.beandiff.core.model.FieldProperty


private class TransformedProperty( // TODO temp aproach, verify & refactor/change
    original: Property,
    transformedValue: Any) extends Property {
  
  var yieldTransformed = true
  
  override def value(target: Any) = {
    if (yieldTransformed)
      transformedValue
    else
      original.value(target)
  }
  
  override def setValue(target: Any, value: Any) = {
    if (yieldTransformed)
      throw new UnsupportedOperationException // TODO
    else
      original.setValue(target, value)
  }
  
  override def toString = original.toString()
  override def hashCode() = original.hashCode()
  override def equals(obj: Any) = original.equals(obj)
}

class TransformingDiffEngine(
    private val delegate: DiffEngine,
    private val transformer: ObjectTransformer,
    private val translators: Map[Class[_ <: Change], ChangeTranslation]) extends DiffEngine { // TODO

  override def calculateDiff(o1: Any, o2: Any): Diff = {
    calculateDiff0(new DiffImpl(o1, Map()), EmptyPath, o1, o2) // FIME creates diff with untransformed target
  }
  
  private[core] override def calculateDiff0(zero: Diff, location: Path, o1: Any, o2: Any) = {
    val t1 = transformer.transform(o1)
    val t2 = transformer.transform(o2)
    
    val transformedProperty = transformProperty(location.last, t1)
    val transformedLocation = location.stepBack.step(transformedProperty)
    
    val diff = delegate.calculateDiff0(zero, transformedLocation, t1, t2)
    
    val result = diff.changes.foldLeft(zero)( // TODO tests
        (diff, propChanges) => {
          val transformedChangeset = transform(propChanges._1, propChanges._2)
          diff.withChanges(propChanges._1, transformedChangeset)
        }) //TODO test withChange(emptyDiff)
        
//    transformedProperty.yieldTransformed = false // FIXME if set to false, then unable to transform changes from outer collection
        
    result
  }
  
  private def transformProperty(prop: Property, transformedValue: Any): TransformedProperty = {
    new TransformedProperty(prop, transformedValue)
  }
  
  private def transform(propToSkip: Property, changes: ChangeSet) = { // TODO tests (e.g. transform(Diff[[0] -> FlatChangeSet[NewValue[1->2]]]))
    val leafChanges = changes.leafChanges
    val transformed = leafChanges.map(transformChange)
    
    transformed.foldLeft(ChangeSet(changes.target))( // TODO check 
        (acc: ChangeSet, pathChange: (Path, Change)) => acc.withChange(pathChange._1, pathChange._2)) // FIXME FIXME FIXME breaks when flatchangeset becomes a Diff (Diff(self -> diff(...))). Add tests & fix 
  }
  
  private def transformChange(pathChange: (Path, Change)): (Path, Change) = {
      pathChange._1 -> transformChange(pathChange._2)
  }
  
  private def transformChange(change: Change): Change = {
    translators.get(change.getClass) match {
      case Some(t) => t.translate(change)
      case None => change
    }
  }
}