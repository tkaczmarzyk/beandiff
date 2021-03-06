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
package org.beandiff.test

import org.scalatest.matchers.Matcher
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.Property


object BeanDiffMatchers { // TODO move to main/src ? // TODO avoid haveSth... (make it have-word compatible if possible)

  def haveDifference: Matcher[Diff] = haveDifference(EmptyPath)
  
  def haveDifference(pathDef: String) = new PathDifferenceMatcher(pathDef)
  
  def haveDifference(path: Path): PathDifferenceMatcher = new PathDifferenceMatcher(path)
  
  def haveDifference(prop: Property): PathDifferenceMatcher = haveDifference(Path(prop))
  
  def haveDeletionAt(index: Int) = new DeletionMatcher(index) // TODO refactor to have(new Deletion(...)
  
  def haveChange(change: Change) = new ChangeMatcher(Specific(change))
  
  def haveChange(pathStr: String, change: Change): ChangeMatcher = haveChange(Path(pathStr), change)
 
  def haveChange(prop: Property, change: Change): ChangeMatcher = haveChange(Path(prop), change)
  
  def haveChange(path: Path, change: Change): ChangeMatcher = new ChangeMatcher(Specific(change), Exact(path))
  
  def haveChanges(changes: Change*) = new ChangeMatcher(changes.map(Specific(_)), StartsWith(EmptyPath))
  
  def haveAnyChange = new ChangeMatcher(pathDef = StartsWith(EmptyPath))
}