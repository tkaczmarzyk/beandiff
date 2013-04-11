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

import java.lang.annotation.Annotation
import scala.annotation.varargs
import org.beandiff.core.CompositeDescendingStrategy
import org.beandiff.core.DelegatingDiffEngine
import org.beandiff.core.DescendingStrategy
import org.beandiff.core.DiffEngine
import org.beandiff.core.EndOnNullStrategy
import org.beandiff.core.LimitedDepthStrategy
import org.beandiff.equality.AnnotationEqualityInvestigator
import org.beandiff.equality.Entity
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.ObjectType
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.support.ClassDictionary
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.core.model.Path
import java.util.ArrayList
import java.util.Arrays
import org.beandiff.core.AcceptEverything
import org.beandiff.core.PathFilter


object DiffEngineBuilder {

  def aDiffEngine(): DiffEngineBuilder = new DiffEngineBuilder

  implicit def builder2engine(builder: DiffEngineBuilder) = builder.build()
}

class DiffEngineBuilder private () {

  private var eqInvestigators: ClassDictionary[EqualityInvestigator] = BeanDiff.DefaultEqInvestigators
  private var endTypes = BeanDiff.DefaultEndTypes
  private var additionalDescStrategy: DescendingStrategy = null
  private var objTypes = new ClassDictionary[ObjectType]()
  private var pathsToSkip = List[Path]()

  def ignoringCase = {
    eqInvestigators = eqInvestigators.withEntry(BeanDiff.IgnoreCase)
    this
  }

  def withDepthLimit(maxDepth: Int) = {
    additionalDescStrategy = new LimitedDepthStrategy(maxDepth)
    this
  }

  def withEntity[T](idField: String, idFields: String*)(implicit m: Manifest[T]): DiffEngineBuilder = {
    withEntity(m.erasure)(idField, idFields: _*)
  }
  
  @varargs
  def withEntity[T](clazz: Class[T])(idField: String, idFields: String*) = {
    objTypes = objTypes.withEntry(clazz -> Entity(new SelectiveEqualityInvestigator(idField, idFields: _*)))
    this
  }

  def withEntity[T, A <: Annotation](clazz: Class[T], idAnno: Class[A])(implicit m: Manifest[A]) = {
    objTypes = objTypes.withEntry(clazz -> Entity(new AnnotationEqualityInvestigator(idAnno)))
    this
  }

  def withEntity[T](idDef: EqualityInvestigator)(implicit m: Manifest[T]): DiffEngineBuilder = {
    withEntity(m.erasure, idDef)
  }
  
  def withEntity[T](clazz: Class[T], idDef: EqualityInvestigator) = {
    objTypes = objTypes.withEntry(clazz -> Entity(idDef))
    this
  }

  def withEndType[T](implicit m: Manifest[T]): DiffEngineBuilder = {
    withEndType(m.erasure)
  }
  
  def withEndType[T](eq: EqualityInvestigator)(implicit m: Manifest[T]): DiffEngineBuilder = {
    withEndType(m.erasure, eq)
  }
  
  def withEndType[T](clazz: Class[T]) = {
    endTypes = endTypes.withLeaf(clazz)
    this
  }

  def withEndType[T](clazz: Class[T], eqDef: EqualityInvestigator): DiffEngineBuilder = {
    withEqualityDef(clazz)(eqDef).withEndType(clazz)
  }

  def `with`[T](clazz: Class[T])(eqDef: EqualityInvestigator) = {
    withEqualityDef(clazz)(eqDef)
  }

  def withEqualityDef[T](clazz: Class[T])(eqDef: EqualityInvestigator) = {
    eqInvestigators = eqInvestigators.withEntry(clazz -> eqDef)
    this
  }

  @varargs
  def skipping(path: String, paths: String*) = {
	pathsToSkip ++= (path :: paths.toList).map(Path(_))
    this
  }

  def skipping(paths: Array[String]) = {
    pathsToSkip ++= paths.toList.map(Path(_))
    this
  }
  
  def build(): DiffEngine = {
    new DelegatingDiffEngine(eqInvestigators, descendingStrategy(), objTypes, filter())
  }

  private def filter() = {
    if (pathsToSkip.isEmpty)
      AcceptEverything
    else
      new PathFilter(pathsToSkip.toSet)
  }
  
  private def descendingStrategy() = {
    if (additionalDescStrategy == null)
      CompositeDescendingStrategy.allOf(new EndOnNullStrategy, endTypes)
    else
      CompositeDescendingStrategy.allOf(new EndOnNullStrategy, additionalDescStrategy, endTypes)
  }
  
}
