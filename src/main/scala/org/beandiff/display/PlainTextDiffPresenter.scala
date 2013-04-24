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

/**
 * Presents a [[org.beandiff.core.model.Diff]] using flat text format. For instance:
 * {{{
 * myList[0].child.name -- 'foo' vs 'bar'
 * myList[1] -- removed 'Child[baz]'
 * myMap[ccc].children[0].name -- 'homer' vs 'marge'
 * myMap -- removed entry: 'bbb' -> 'BBB'
 * myMap[aaa] -- initialized with 'AAA'
 * }}}
 * 
 * @author Tomasz Kaczmarzyk
 */
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
   * @param a separator to set
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
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setRemovalLabel(label: String) = {
    removalLabel = label
    this
  }
  
  /**
   * Sets the label to be displayed before a value added to a set.
   * 
   * Default label is 'added ', e.g.:
   * {{{
   * childrenSet -- added 'Child[name="test"]'
   * }}}
   * 
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setAdditionLabel(label: String) = {
    additionLabel = label
    this
  }
  
  /**
   * Sets the label to be displayed when an object doesn't
   * have a property. It might happen for example when an instance of a base
   * class is to be compared with an instance of a subclass (and a subclass
   * has more fields than a base class).
   * 
   * Default label is 'nothing (no such path)', e.g.:
   * {{{
   * name -- 'Gambit' vs nothing (no such path)
   * }}}
   * 
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setNoPathLabel(label: String) = {
    noPathLabel = label
    this
  }
  
  /**
   * Sets the label to be displayed before an entry added to a map.
   * 
   * Default label is 'initialized with ', e.g.:
   * {{{
   * beansByName[abc] -- initialized with 'Bean[name=abc]'
   * }}}
   * 
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setAssociationLabel(label: String) = {
    associationLabel = label
    this
  }
  
  /**
   * Sets the label to be displayed before an entry removed from a map.
   * 
   * Default label is 'removed entry: ', e.g.:
   * {{{
   * beansByName -- removed entry: [abc] -> 'Bean[name=abc]'
   * }}}
   * 
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setKeyRemovalLabel(label: String) = {
    keyRemovalLabel = label
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
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setDeletionLabel(label: String) = {
    deletionLabel = label
    this
  }
  
  /**
   * Sets a dictionary with [[org.beandiff.display.ToString]]s, which will be
   * used to present elements on [[org.beandiff.core.model.Path]]s.
   * 
   * It's relevant for presenting paths that include Sets. The default way of presenting
   * such paths is:
   * {{{
   * mySet[].name
   * }}}
   * which may be ambiguous. It's good to present the target element of a Set between
   * the braces:
   * {{{
   * mySet[elemA].name
   * }}}
   * Such representation should be concise though, so most of the `toString`
   * implementations are not a good candidates. Hence the [[org.beandiff.display.ToString]]s
   * with appropriate implementation are required.
   * 
   * The dictionary being set is expected to have a value for any class,
   * therefore it's recommended to initialize it with a default value.
   * 
   * @param dictionary with ToStrings mapped to Classes
   * @return this presenter instance (for method chaining)
   */
  def setPathToStrings(dict: ClassDictionary[ToString]) = {
    pathToStrings = dict.map(toStr => (o => toStr.mkString(o)))
    this
  }
  
  /**
   * Sets the string which will be prepended and appended to each value
   * 
   * Default value is a single quote ('), e.g.:
   * {{{
   * name -- 'foo' vs 'bar'
   * }}}
   * 
   * @param a quote to set
   * @return this presenter instance (for method chaining)
   */
  def setValueQuote(quote: String) = {
    valueQuote = quote
    this
  }
  
  /**
   * Sets the label to be displayed before a value inserted to a list.
   * 
   * Default label is 'inserted ', e.g.:
   * {{{
   * children -- inserted 'Child[name="test"]' at [0]
   * }}}
   * 
   * @param a label to set
   * @return this presenter instance (for method chaining)
   */
  def setInsertionLabel(label: String) = {
    insertionLabel = label
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