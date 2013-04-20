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
import java.util.Date
import scala.annotation.varargs
import org.beandiff.TypeDefs.JBigDecimal
import org.beandiff.core.AcceptEverything
import org.beandiff.core.CompositeDescendingStrategy
import org.beandiff.core.DelegatingDiffEngine
import org.beandiff.core.DescendingStrategy
import org.beandiff.core.DiffEngine
import org.beandiff.core.EndOnNullStrategy
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.core.LimitedDepthStrategy
import org.beandiff.core.PathFilter
import org.beandiff.core.model.Path
import org.beandiff.equality.AnnotationEqualityInvestigator
import org.beandiff.equality.ComparableEqualityInvestigator
import org.beandiff.equality.Entity
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.IgnoreCaseStringEqualityInvestigator
import org.beandiff.equality.ObjectType
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.support.ClassDictionary
import org.beandiff.support.ValueTypes
import org.beandiff.DiffEngineBuilder._
import java.util.Collection


object DiffEngineBuilder {

  private final val IgnoreCase = (classOf[String], new IgnoreCaseStringEqualityInvestigator)
  
  final val DefaultEndTypes = EndOnSimpleTypeStrategy
		  .withLeaf(classOf[JBigDecimal])
		  .withLeaf(classOf[Date])
		  .withLeaf(classOf[Class[_]])
  
  final val DefaultDescStrategy = CompositeDescendingStrategy.allOf(
      new EndOnNullStrategy(), DefaultEndTypes)


  final val DefaultEqInvestigators: ClassDictionary[EqualityInvestigator] = new ClassDictionary(new StdEqualityInvestigator)
    .withEntry(classOf[JBigDecimal] -> new ComparableEqualityInvestigator)
    .withEntries(ValueTypes.all.map((_, new StdEqualityInvestigator)))
  
    
  def aDiffEngine(): DiffEngineBuilder = new DiffEngineBuilder

  implicit def builder2engine(builder: DiffEngineBuilder) = builder.build()
}

class DiffEngineBuilder private () {

  private var eqInvestigators: ClassDictionary[EqualityInvestigator] = DefaultEqInvestigators
  private var endTypes = DefaultEndTypes
  private var additionalDescStrategy: DescendingStrategy = null
  
  private var classDifferenceHandler = new DescendingStrategy {
    override def shouldProceed(path: Path, o1: Any, o2: Any) = {
      val col1 = classOf[Collection[Any]].isAssignableFrom(o1.getClass)
      val col2 = classOf[Collection[Any]].isAssignableFrom(o2.getClass)
      (col1 && col2) || (!col1 && !col2)
    }
  }
  
  private var objTypes = new ClassDictionary[ObjectType]()
  private var pathsToSkip = List[Path]()

  /**
   * The DiffEngine being build will be case-insensitive
   *  when comparing String values.
   * 
   * @return this builder instance (for method chaining)
   */
  def ignoringCase = {
    eqInvestigators = eqInvestigators.withEntry(IgnoreCase)
    this
  }

  /**
   * Sets the depth limit for property traversal.
   * 
   * For instance consider that diff has to be calculated for instances of the following class:
   * {{{
   * class A {
   *   private String name;
   *   private A delegate;
   * }
   * }}}
   * 
   * When there is no depth limit, the diff engine will step down to properties
   * of the delegate object (and potentially delegate's delegate and so on). 
   * 
   * With depth limit set to 2, the delegates' name will be compared, but the engine
   *  will not step to investigate delegates' delegate properties -- it will just test
   *  them for equality.
   * 
   * With depth limit set to 1, the engine will not step to delegates' properties,
   *  it will just both field pairs of targets for equality.
   * 
   * When set to zero, the resulting diff engine will just use equals 
   *  (or more precisely, the configured [[org.beandiff.equality.EqualityInvestigator]])
   *   to compare the targets.
   * 
   * @param maxDepth maximum allowed path depth
   * @return this builder instance (for method chaining)
   */
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

  /**
   * Specifies path(s) (as in [[org.beandiff.core.model.Path]])
   * to be excluded from diff calculation.
   * 
   * If a path is skipped, then any difference on that path will not
   * be present in the result [[org.beandiff.core.model.Diff]]s.
   * 
   * @param path the path to be excluded
   * @param paths additional paths to skip
   * @return this builder instance (for method chaining)
   */
  @varargs
  def skipping(path: String, paths: String*) = {
	pathsToSkip ++= (path :: paths.toList).map(Path(_))
    this
  }

  /**
   * Specifies paths (as in [[org.beandiff.core.model.Path]])
   * to be excluded from diff calculation.
   * 
   * If a path is skipped, then any difference on that path will not
   * be present in the result [[org.beandiff.core.model.Diff]].
   * 
   * @param paths list of paths to skip
   * @return this builder instance (for method chaining)
   */
  def skipping(paths: Array[String]) = {
    pathsToSkip ++= paths.toList.map(Path(_))
    this
  }
  
  /**
   * The engine being built will not step into properties
   * whenever the classes of targets are different.
   * 
   * For example, consider the following classes:
   * {{{
   * class Foo {
   *   String name = "foo";
   *   int value = 1;
   * }
   * 
   * class Bar {
   *   String name = "bar";
   *   int value = 2;
   * }
   * }}}
   * 
   * This invocation:
   * {{{BeanDiff.diffEngine().build().calculateDiff(new Foo(), new Bar())
   * }}}
   * would match properties by name and compare them with each other
   * even though the classes are different. The result would be 2 differences:
   * one on path `name`, the other on path `value`.
   * 
   * On the contrary, using `breakWhenClassesDifferent` as follows:
   * {{{BeanDiff.diffEngine()
   *   .breakWhenClassesDifferent()
   *   .build().calculateDiff(new Foo(), new Bar())
   * }}}
   * would produce just a single difference, without inspecting
   * the properties.
   * 
   * @return this builder instance (for method chaining)
   */
  def breakWhenClassesDifferent() = {
    classDifferenceHandler = new DescendingStrategy {
      override def shouldProceed(path: Path, o1: Any, o2: Any) = {
        o1.getClass == o2.getClass
      }
    }
    this
  }
  
  /**
   * Returns the [[org.beandiff.core.DiffEngine]] being built.
   * 
   * Each invocation returns a new engine instance. The engine, once returned,
   * will not be affected by further method invocations on this builder. It's 
   * allowed to call this method multiple times to obtain many engines.
   * 
   * @return a [[org.beandiff.core.DiffEngine]] instance
   */
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
      CompositeDescendingStrategy.allOf(new EndOnNullStrategy, classDifferenceHandler, endTypes)
    else
      CompositeDescendingStrategy.allOf(new EndOnNullStrategy, additionalDescStrategy, classDifferenceHandler, endTypes)
  }
  
}
