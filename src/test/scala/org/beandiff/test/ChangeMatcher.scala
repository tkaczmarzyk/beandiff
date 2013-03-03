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

import org.beandiff.core.model._
import org.beandiff.core.model.Path.EmptyPath
import org.scalatest.matchers._
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.Deletion

class ChangeMatcher(
    changeDefs: Seq[ChangeDef],
    pathDef: PathDef) extends Matcher[Diff] {

  def this(changeDef: ChangeDef = AnyChange(), pathDef: PathDef = Exact(EmptyPath)) = {
    this(List(changeDef), pathDef)
  }
  
  def apply(left: Diff): MatchResult = {
    var notSeen = List[ChangeDef]()
    for (changeDef <- changeDefs) {
      val filtered = left.leafChanges.filter(pathChange => pathDef.matches(pathChange._1) &&  changeDef.matches(pathChange._2))
      if (filtered.isEmpty) {
        notSeen ::= changeDef
      }
    }
    MatchResult(notSeen.isEmpty, "Diff doesn't have " + notSeen.mkString(", ") + " at " + pathDef + ":\n" + left,
        "Diff does have " + notSeen.mkString(", ") + " at " + pathDef + ":\n" + left)
  }
}