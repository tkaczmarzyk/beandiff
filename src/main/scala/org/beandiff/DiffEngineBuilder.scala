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
package org.beandiff

import org.beandiff.core.DiffEngine
import org.beandiff.core.DescendingStrategy
import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.core.DelegatingDiffEngine
import org.beandiff.core.DiffEngine
import org.beandiff.equality.ObjectType
import org.beandiff.core.LimitedDepthStrategy
import org.beandiff.core.CompositeDescendingStrategy
import org.beandiff.core.EndOnNullStrategy
import org.beandiff.core.EndOnSimpleTypeStrategy


object DiffEngineBuilder {
  
  def aDiffEngine(): DiffEngineBuilder = null
  
  implicit def builder2engine(builder: DiffEngineBuilder) = builder.build() 
}

class DiffEngineBuilder {

  private var eqInvestigators: ClassDictionary[EqualityInvestigator] = BeanDiff.DefaultEqInvestigators
  private var descStrategy: DescendingStrategy = BeanDiff.DefaultDescStrategy
  private var objTypes: ClassDictionary[ObjectType] = null
  
  def ignoringCase = {
    eqInvestigators = eqInvestigators.withEntry(BeanDiff.IgnoreCase)
    this
  }
  
  def withDepthLimit(maxDepth: Int) = {
    descStrategy = CompositeDescendingStrategy.allOf(new LimitedDepthStrategy(maxDepth), BeanDiff.DefaultDescStrategy)
    this
  }
  
  def build(): DiffEngine = new DelegatingDiffEngine(eqInvestigators, descStrategy)
  
}