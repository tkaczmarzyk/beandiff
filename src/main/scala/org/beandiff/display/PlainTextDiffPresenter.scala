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
package org.beandiff.display

import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Path
import org.beandiff.core.model._
import org.beandiff.core.model.change._
import org.beandiff.core.model.change.ChangeOrdering


class PlainTextDiffPresenter(
  private val pathValueSeparator: String = " -- ",
  private val valuesSeparator: String = " vs ",
  private val valueQuote: String = "'",
  private val differenceSeparator: String = "\n",
  private val noPathLabel: String = "nothing (no such path)",
  private val keyRemovalLabel: String = "removed entry: ",
  private val associationLabel: String = "initialized with ") extends DiffPresenter {


  override def present(d: Diff): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder

      for ((path, change) <- d.leafChanges.sorted(PathChangeOrdering)) { // TODO temporary amendments to the new model
        change match { // FIXME generic presentation to avoid so many cases // TODO visitor pattern? // TODO use sealed classes?
          case Deletion(x, index) => {
            result.append(path.withIndex(index).mkString).append(pathValueSeparator).append("deleted")
          }
          
          case Insertion(x, index) => {
            result.append(path.mkString).append(pathValueSeparator)
            result.append("inserted ").append(present(x))
            result.append(" at ").append(IndexProperty(index).mkString)
          }
          
          case Addition(x) => {
            result.append(path.mkString).append(pathValueSeparator);
            result.append("added ").append(present(x))
          }
          
          case Removal(x) => {
            result.append(path.mkString).append(pathValueSeparator);
            result.append("removed ").append(valueQuote).append(x).append(valueQuote)
          }
          
          case NewValue(prop, oldVal, newVal) => {
            result.append(path.step(prop).mkString).append(pathValueSeparator)
	        result.append(present(oldVal))
	        result.append(valuesSeparator)
	        result.append(present(newVal))
          }
          
          case Shift(x, oldIndex, newIndex) => {
            result.append(path.mkString).append(pathValueSeparator)
            result.append(present(x))
            result.append(" moved from ").append(IndexProperty(oldIndex).mkString)
            result.append(" to ").append(IndexProperty(newIndex).mkString)
          }
          
          case KeyRemoval(key, oldVal) => {
            result.append(path.mkString).append(pathValueSeparator)
            result.append(keyRemovalLabel).append(present(key))
            result.append(" -> ").append(present(oldVal))
          }
          
          case Association(key, value) => {
            result.append(path.step(change.targetProperty).mkString).append(pathValueSeparator)
            result.append(associationLabel).append(present(value))
          }
          
          case x => {
            result.append(path.mkString).append(pathValueSeparator)
	        result.append(present(change.oldValue))
	        result.append(valuesSeparator)
	        result.append(present(change.newValue))
          }
        }
        
        result.append(differenceSeparator)
      }

      result.toString
    }
  }
  
  private def present(value: Any) = {
    value match {
      case None => noPathLabel
      case Some(x) => valueQuote + x + valueQuote
      case v => valueQuote + v + valueQuote
    }
  }
}