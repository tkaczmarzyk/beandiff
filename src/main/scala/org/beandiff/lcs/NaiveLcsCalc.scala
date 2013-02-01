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
package org.beandiff.lcs

import org.beandiff.TypeDefs.JList
import org.beandiff.support.:+
import org.beandiff.equality.EqualityInvestigator

class NaiveLcsCalc(
    private val id: EqualityInvestigator) extends LcsCalc {

  override def lcs(xs: Seq[Any], ys: Seq[Any]): Seq[Occurence] = {
    if (xs.isEmpty || ys.isEmpty)
      Vector()
    else (xs, ys) match {
      case (xs1 :+ x, ys1 :+ y) =>
        if (id.areEqual(x, y))
          lcs(xs1, ys1) :+ Occurence(x, xs.length - 1, ys.length - 1)
        else {
          val lcs1 = lcs(xs1, ys)
          val lcs2 = lcs(xs, ys1)
          
          if (lcs1.length > lcs2.length) lcs1
          else lcs2
        }
    }
  }
}