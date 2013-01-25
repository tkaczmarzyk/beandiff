/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Diff
import org.scalatest.matchers.MatchResult

class PathDifferenceMatcher(
    private val path: Path) extends Matcher[Diff] {

  def this(pathDef: String) = this(Path.of(pathDef))
  
  def apply(left: Diff): MatchResult =
    MatchResult(left.hasDifference(path), mkMsg("The diff doesnt have differenece", left), mkMsg("The diff has difference", left))
    
  private def mkMsg(prefix: String, target: Diff) =
    if (path == EmptyPath)
      prefix + ":\n" + target
    else
      prefix + " on " + path + ":\n" + target
}