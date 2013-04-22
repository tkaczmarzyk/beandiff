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
import org.beandiff.display.PlainTextDiffPresenter._
import org.beandiff.support.ClassDictionary
import org.beandiff.support.ToString


object PlainTextDiffPresenter {
  val DefaultPathValueSeparator = " -- "
  val DefaultValueSeparator = " vs "
  val DefaultValueQuote = "'"
  val DefaultDifferenceSeparator = "\n"
  val DefaultNoPathLabel = "nothing (no such path)"
  val DefaultKeyRemovalLabel = "removed entry: "
  val DefaultAssociationLabel = "initialized with "
  val DefaultPathFormat = "%s"
  val DefaultDeletionLabel = "deleted "
  val DefaultRemovalLabel = "removed "
  val DefaultInsertionLabel = "inserted "
  val DefaultAdditionLabel = "added "
}

class PlainTextDiffPresenter(
  private var pathValueSeparator: String = DefaultPathValueSeparator,
  private var valuesSeparator: String = DefaultValueSeparator,
  private var valueQuote: String = DefaultValueQuote,
  private var differenceSeparator: String = DefaultDifferenceSeparator,
  private var noPathLabel: String = DefaultNoPathLabel,
  private var keyRemovalLabel: String = DefaultKeyRemovalLabel,
  private var associationLabel: String = DefaultAssociationLabel,
  private var pathFormat: String = DefaultPathFormat,
  private var deletionLabel: String = DefaultDeletionLabel,
  private var removalLabel: String = DefaultRemovalLabel,
  private var insertionLabel: String = DefaultInsertionLabel,
  private var additionLabel: String = DefaultAdditionLabel,
  private var pathToStrings: ClassDictionary[Any => String] = null) extends DiffPresenter {


  def this() = { // additional constructor to make java users happy :P
    this(pathValueSeparator = DefaultPathValueSeparator)
  }

  
  override def present(d: Diff): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder

      for ((path, change) <- d.leafChanges.sorted(PathChangeOrdering)) { // TODO temporary amendments to the new model
        change match { // FIXME generic presentation to avoid so many cases // TODO visitor pattern? // TODO use sealed classes?
          case Deletion(x, index) => {
            result.append(format(path.withIndex(index))).append(pathValueSeparator)
            result.append(deletionLabel).append(present(x))
          }
          
          case Insertion(x, index) => {
            result.append(format(path)).append(pathValueSeparator)
            result.append(insertionLabel).append(present(x))
            result.append(" at ").append(IndexProperty(index).mkString)
          }
          
          case Addition(x) => {
            result.append(format(path)).append(pathValueSeparator);
            result.append(additionLabel).append(present(x))
          }
          
          case Removal(x) => {
            result.append(format(path)).append(pathValueSeparator);
            result.append(removalLabel).append(valueQuote).append(x).append(valueQuote)
          }
          
          case NewValue(prop, oldVal, newVal) => {
            result.append(format(path.step(prop))).append(pathValueSeparator)
	        result.append(present(oldVal))
	        result.append(valuesSeparator)
	        result.append(present(newVal))
          }
          
          case Shift(x, oldIndex, newIndex) => {
            result.append(format(path)).append(pathValueSeparator)
            result.append(present(x))
            result.append(" moved from ").append(IndexProperty(oldIndex).mkString)
            result.append(" to ").append(IndexProperty(newIndex).mkString)
          }
          
          case KeyRemoval(key, oldVal) => {
            result.append(format(path)).append(pathValueSeparator)
            result.append(keyRemovalLabel).append(present(key))
            result.append(" -> ").append(present(oldVal))
          }
          
          case Association(key, value) => {
            result.append(format(path.step(change.targetProperty))).append(pathValueSeparator)
            result.append(associationLabel).append(present(value))
          }
          
          case x => {
            result.append(format(path)).append(pathValueSeparator)
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
  
  /**
   * Sets a format (as in [[java.lang.String.format]]) for
   * [[org.beandiff.core.model.Path]] presentation.
   * 
   * The format is expected to contain arbitrary content
   * with a single '%s', which will be replaced with a string
   * representations of paths. See [[java.lang.String.format]]
   * for more details.
   * 
   * By default the format is just `"%s"`, i.e. simple
   * string representation will be used.
   * 
   * @return this presenter instance (for method chaining)
   */
  def setPathFormat(format: String) = {
    pathFormat = format
    this
  }
  
  /**
   * Sets the separator to be displayed between divergent
   * property values of the target objects. 
   * 
   * Default separator is ' vs ', e.g.:
   * {{{
   * children[0].name -- 'test' vs null
   * }}}
   * 
   * @return this presenter instance (for method chaining)
   */
  def setValueSeparator(separator: String) = {
    valuesSeparator = separator
    this
  }
  
  /**
   * Sets the separator to be displayed between differences.
   * 
   * Default separator is new-line character, i.e. each difference
   * is printed in separate line. 
   * 
   * @return this presenter instance (for method chaining)
   */
  def setDifferenceSeparator(separator: String) = {
    differenceSeparator = separator
    this
  }
  
  /**
   * Sets the separator to be displayed between paths and values.
   * 
   * Default separator is ' -- ', e.g.:
   * {{{
   * children[0].name -- 'test' vs 'null'
   * }}}
   * 
   * @return this presenter instance (for method chaining)
   */
  def setPathValueSeparator(separator: String) = {
    pathValueSeparator = separator
    this
  }
  
  /**
   * Sets the label to be displayed before a value removed from a set.
   * 
   * Default label is 'removed ', e.g.:
   * {{{
   * childrenSet -- removed 'Child[name="test"]'
   * }}}
   * 
   * @return this presenter instance (for method chaining)
   */
  def setRemovalLabel(label: String) = {
    removalLabel = label
    this
  }
  
  /**
   * Sets the label to be displayed before a value deleted from a list.
   * 
   * Default label is 'deleted ', e.g.:
   * {{{
   * children[0] -- deleted 'Child[name="test"]'
   * }}}
   * 
   * @return this presenter instance (for method chaining)
   */
  def setDeletionLabel(label: String) = {
    deletionLabel = label
    this
  }
  
  def setPathToStrings(dict: ClassDictionary[ToString]) = { // TODO doc, better name
    pathToStrings = dict.map(toStr => (o => toStr.mkString(o)))
    this
  }
  
  private def present(value: Any) = {
    value match {
      case None => noPathLabel
      case Some(x) => valueQuote + x + valueQuote
      case v => valueQuote + v + valueQuote
    }
  }
  
  private def format(path: Path) = {
    val pathStr = if (pathToStrings == null) path.mkString else path.mkString(pathToStrings)
    String.format(pathFormat, pathStr)
  }
}