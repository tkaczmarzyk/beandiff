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
import org.scalatest.matchers._
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.Deletion

class DeletionMatcher(
    index: Int) extends Matcher[Traversable[(Path, Change)]] {

  def apply(left: Traversable[(Path, Change)]): MatchResult = {
    val filtered = left.filter(pathChange => pathChange._2.isInstanceOf[Deletion] && pathChange._2.asInstanceOf[Deletion].index == this.index)
    MatchResult(!filtered.isEmpty, "ChangeSet doesn't have a deletion at " + index, "ChangeSet does have a deletion at " + index)
  }
}