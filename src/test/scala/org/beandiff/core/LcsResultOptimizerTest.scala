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

import org.beandiff.TestDefs._
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Diff
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.test.JList
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.Property
import org.beandiff.core.model.Path
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Shift
import org.beandiff.core.model.Self
import org.beandiff.core.model.change.NewValue
import org.mockito.Mockito
import org.mockito.Matchers
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Insertion
import org.mockito.invocation.InvocationOnMock
import org.beandiff.core.model.change.NewValue
import org.beandiff.equality.Entity
import org.beandiff.equality.Value
import org.beandiff.support.ClassDictionary
import org.beandiff.equality.ObjectType
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.equality.ObjectType
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.beans.Simpsons
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.PathChangeOrdering
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion

@RunWith(classOf[JUnitRunner])
class LcsResultOptimizerTest extends FunSuite with ShouldMatchers with Simpsons {

  private final val EverythingIsAValueWithNameId = new ClassDictionary[ObjectType](Value(new SelectiveEqualityInvestigator("name")))
  
  private val lcsEngine = mock[LcsDiffEngine]
  private val parent = mock[DiffEngineCoordinator]
  
  when(lcsEngine.objTypes).thenReturn(EverythingIsEntityWithNameId)
  
  private val optimizer = new LcsResultOptimizer(parent, lcsEngine)
  
  private val simpsons = JList(bart, lisa, maggie)

  
  test("should merge insertion and deletion into new-value when index property is the same and identity different") {
    val children = JList(bart, lisa, milhouse)
    when(lcsEngine.calculateDiff(simpsons, children)).thenReturn(Diff(simpsons,
        Deletion(maggie, 2), Insertion(milhouse, 2)))
    
    val expectedDiff = Diff(simpsons, NewValue(Property("[2]"), maggie, milhouse))
        
    optimizer.calculateDiff(simpsons, children) should be === expectedDiff
  }
  
  test("should merge insert with delete into sub-diff when index and identity are the same") {
    val simps2 = JList(bart, lisa, maggie2)
    when(lcsEngine.calculateDiff(simpsons, simps2)).thenReturn(Diff(simpsons,
        Deletion(maggie, 2), Insertion(maggie2, 2)))
    
    val expectedDiff = Diff(simpsons, Property("[2]") -> Diff(maggie, NewValue(Property("value"), 1, 2)))
        
    when(parent.calculateDiff(Diff(simpsons), Property("[2]"), maggie, maggie2)).thenReturn(expectedDiff)
        
    optimizer.calculateDiff(simpsons, simps2) should be === expectedDiff
  }
  
  test("should merge insert with delete into shift when indentity is the same") {
    val simps2 = JList(bart, maggie, lisa)
    when(lcsEngine.calculateDiff(simpsons, simps2)).thenReturn(Diff(simpsons,
        Deletion(lisa, 1), Insertion(lisa, 2)))
    
    val expectedDiff = Diff(simpsons, Shift(lisa, 1, 2))
        
    when(parent.calculateDiff(anyDiff, anyProp, any, any)).thenAnswer(unchangedDiff())
        
    optimizer.calculateDiff(simpsons, simps2) should be === expectedDiff
  }
  
  test("should calculate a subdiff for the shifted element") {
    val simps2 = JList(bart, maggie, lisa2)
    when(lcsEngine.calculateDiff(simpsons, simps2)).thenReturn(Diff(simpsons,
        Deletion(lisa, 1), Insertion(lisa2, 2)))
    
    val expectedShift = Diff(simpsons, Shift(lisa, 1, 2))
    val expectedDiff = expectedShift.withChange(Path("[1]"), new NewValue(Property("value"), 8, 9))
        
    when(parent.calculateDiff(expectedShift, Property("[1]"), lisa, lisa2)).thenReturn(expectedDiff)
    
    optimizer.calculateDiff(simpsons, simps2) should be === expectedDiff
  }
  
  test("should not use the same change in 2 optimizations") {
    val simps2 = JList(bart, lisa2, maggie, lisa)
    when(lcsEngine.calculateDiff(simpsons, simps2)).thenReturn(Diff(simpsons,
        Deletion(lisa, 1), Insertion(lisa2, 1), Insertion(lisa, 3)))
    
    val subDiff = Diff(lisa, NewValue(Property("value"), 8, 9))
    val expectedDiff = Diff(simpsons, Self -> Diff(simpsons, Insertion(lisa, 3)),
        Property("[1]") -> subDiff)
    
    when(parent.calculateDiff(anyDiff, of(Property("[1]")), of(lisa), of(lisa2))).thenAnswer(
        (inv: InvocationOnMock) => inv.getArguments()(0).asInstanceOf[Diff].withChanges(Property("[1]"), subDiff))
        
    optimizer.calculateDiff(simpsons, simps2) should be === expectedDiff
  }
  
  test("should prefer merging into subdiff than into shift") {
    val simps2 = JList(bart, lisa2, maggie, lisa)
    when(lcsEngine.calculateDiff(simpsons, simps2)).thenReturn(Diff(simpsons,
        new Deletion(lisa, 1), Insertion(lisa, 3), Insertion(lisa2, 1))) // traversation in this order would cause finding shift pair first
    
    val subDiff = Diff(lisa, NewValue(Property("value"), 8, 9))
    val expectedDiff = Diff(simpsons, Self -> Diff(simpsons, Insertion(lisa, 3)),
        Property("[1]") -> subDiff)
    
    when(parent.calculateDiff(anyDiff, of(Property("[1]")), of(lisa), of(lisa2))).thenAnswer(
        (inv: InvocationOnMock) => inv.getArguments()(0).asInstanceOf[Diff].withChanges(Property("[1]"), subDiff))
        
    optimizer.calculateDiff(simpsons, simps2) should be === expectedDiff
  }
  
  test("should merge insert with delete when they are value objects (no identity)") {
    when(lcsEngine.objTypes).thenReturn(EverythingIsAValueWithNameId)
    
    val children = JList(bart, lisa, milhouse)
    when(lcsEngine.calculateDiff(simpsons, children)).thenReturn(Diff(simpsons,
        Deletion(maggie, 2), Insertion(milhouse, 2)))
    
    val expectedDiff = Diff(simpsons, Property("[2]") -> Diff(maggie,
        NewValue(Property("name"), "maggie", "milhouse"), NewValue(Property("value"), 1, 10)))
        
    when(parent.calculateDiff(Diff(simpsons), Property("[2]"), maggie, milhouse)).thenReturn(expectedDiff)
        
    optimizer.calculateDiff(simpsons, children) should be === expectedDiff
  }
  
  test("should not optimize into shift if there are other changes between each pair") { // otherwise the order would be incorrect
    val children = JList(maggie, milhouse, bart)
    val rawDiff = Diff(simpsons,
        Deletion(bart, 0), Deletion(lisa, 1), Insertion(milhouse, 1), Insertion(bart, 2))
        
    when(lcsEngine.calculateDiff(simpsons, children)).thenReturn(rawDiff)
    
    optimizer.calculateDiff(simpsons, children) should be === rawDiff
  }
  
  test("should not optimize if changes are at different paths") { // nulls should be safe here, since nothing is expected to be optimized
    val rawDiff = Diff(null, Self -> Diff(null, new Insertion(null, 0)),
        Property("[0]") -> Diff(null, new Deletion(null, 0)))
        
    when(lcsEngine.calculateDiff(null, null)).thenReturn(rawDiff)
    
    optimizer.calculateDiff(null, null) should be === rawDiff 
  }
  
  // TODO should perform new-value even though... as long as...
}