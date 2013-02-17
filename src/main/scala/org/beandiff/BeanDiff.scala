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

import java.io.PrintWriter
import org.beandiff.TypeDefs.JBigDecimal
import org.beandiff.core.DelegatingDiffEngine
import org.beandiff.core.EndOnNullStrategy
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.core.model.Diff
import org.beandiff.display.PlainTextDiffPresenter
import org.beandiff.equality.ComparableEqualityInvestigator
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.IgnoreCaseStringEqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.support.ClassDictionary
import org.beandiff.support.ValueTypes
import java.io.PrintStream
import org.beandiff.core.DiffEngine

/**
 * A container for syntactic sugar methods
 *
 * @author Tomasz Kaczmarzyk
 */
object BeanDiff {

  private type EqInvestigatorBinding = (Class[_], EqualityInvestigator);
  
  final val DefaultDescStrategy = new EndOnNullStrategy(EndOnSimpleTypeStrategy.withLeaf(classOf[JBigDecimal]))
  
  final val DefaultPresenter = new PlainTextDiffPresenter
  
  final val DefaultEqInvestigators = new ClassDictionary(new StdEqualityInvestigator)
    .withEntry(classOf[JBigDecimal] -> new ComparableEqualityInvestigator)
  	.withEntries(ValueTypes.all.map((_, new StdEqualityInvestigator)))
  
  final val IgnoreCase = (classOf[String], new IgnoreCaseStringEqualityInvestigator)

    
  def diff(o1: Any, o2: Any): Diff = {
    diff(o1, o2, List() : _*)
  }

  def diff(o1: Any, o2: Any, modifiers: Any*): Diff = {
    diffEngine(modifiers: _*).calculateDiff(o1, o2)
  }
  
  def diffEngine(modifiers: Any*): DiffEngine = {
    val eqInvestigators = DefaultEqInvestigators.withEntries(getEqInvestigatorMappings(modifiers.toList))

    new DelegatingDiffEngine(eqInvestigators, DefaultDescStrategy)
  }
  
  def print(diff: Diff): Unit = print(new PrintWriter(System.out), diff)
  
  def print(out: PrintWriter, diff: Diff): Unit = {
    out.println(DefaultPresenter.present(diff))
  }
  
  def printDiff(o1: Any, o2: Any): Unit = 
    printDiff(o1, o2, List() : _*)
  
  def printDiff(o1: Any, o2: Any, modifiers: Any*): Unit =
    printDiff(new PrintWriter(System.out), o1, o2, modifiers : _*)
  
  def printDiff(out: PrintStream, o1: Any, o2: Any): Unit =
    printDiff(new PrintWriter(out), o1, o2)
    
  def printDiff(out: PrintWriter, o1: Any, o2: Any): Unit =
    printDiff(out, o1, o2, List() : _*)
  
  def printDiff(out: PrintStream, o1: Any, o2: Any, modifiers: Any*) =
    print(new PrintWriter(out), diff(o1, o2, modifiers : _*))
    
  def printDiff(out: PrintWriter, o1: Any, o2: Any, modifiers: Any*) =
    print(out, diff(o1, o2, modifiers : _*))

  private def getEqInvestigatorMappings(objects: List[_]) = {
    objects.filter(_.isInstanceOf[EqInvestigatorBinding])
      .asInstanceOf[Iterable[EqInvestigatorBinding]]
  }
}