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
package org.beandiff.core.model.change

object ChangeOrdering extends Ordering[Change] {

  def compare(ch1: Change, ch2: Change): Int = { // TODO refactor
    (ch1, ch2) match {
      case (Shift(_, fromIdx, _), Deletion(_, idx)) => - fromIdx.compareTo(idx)
      case (Deletion(_, idx), Shift(_, fromIdx, _)) => - idx.compareTo(fromIdx)
      case (Shift(_, _, toIdx), Insertion(_, idx)) => toIdx.compareTo(idx)
      case (Insertion(_, idx), Shift(_, _, toIdx)) => idx.compareTo(toIdx)
      case (Deletion(_, _), Insertion(_, _)) => -1
      case (Insertion(_, _), Deletion(_, _)) => 1
      case (Removal(_), Addition(_)) => -1
      case (Addition(_), Removal(_)) => 1
      case (Insertion(_, idx1), Insertion(_, idx2)) => idx1.compare(idx2)
      case (Deletion(_, idx1), Deletion(_, idx2)) => - idx1.compareTo(idx2)
      case (NewValue(p1, _, _), NewValue(p2, _, _)) => p1.mkString.compareTo(p2.mkString)
      case (NewValue(_, _, _), _) => -1
      case (_, NewValue(_, _, _)) => 1
      case _ => 0
    }
  }
}