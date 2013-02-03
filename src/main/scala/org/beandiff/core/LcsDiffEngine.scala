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
package org.beandiff.core

import scala.collection.JavaConversions.asScalaBuffer
import org.beandiff.TypeDefs.JList
import org.beandiff.core.model.Diff
import org.beandiff.core.model.DiffImpl
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.lcs.LcsCalc
import org.beandiff.support.CollectionSupport.convert
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion


class LcsDiffEngine(
  private val delegate: DiffEngine,
  private val lcsCalc: LcsCalc) extends DiffEngine {

  def calculateDiff(o1: Any, o2: Any) = {
    val zero = new DiffImpl(EmptyPath, o1, Map())
    calculateDiff0(zero, EmptyPath, o1, o2)
  }

  private[core] def calculateDiff0(zero: Diff, location: Path, o1: Any, o2: Any): Diff = {
    val xs = o1.asInstanceOf[JList]
    val ys = o2.asInstanceOf[JList]

    val lcs = lcsCalc.lcs(xs, ys)

    val deleted = xs.dropIndices(lcs.map(_.index1))
    val inserted = ys.dropIndices(lcs.map(_.index2))

    val dels = deleted.foldLeft(zero)(withChange(location, zero, new Deletion(_, _)))
    val delsAndInserts = inserted.foldLeft(dels)(withChange(location, zero, new Insertion(_, _)))
    
    lcs.foldLeft(delsAndInserts)(
        (accDiff, occurence) => {
          val ver1 = xs.get(occurence.index1)
          val ver2 = ys.get(occurence.index2)
          delegate.calculateDiff0(accDiff, location.withIndex(occurence.index1), ver1, ver2)
        }
    )
  }

  private def withChange(location: Path, acc: Diff, changeGen: (Any, Int) => Change)(accDiff: Diff, elemWithIndex: (Any, Int)) = {
    elemWithIndex match {
      case (elem, index) => accDiff.withChange(location, changeGen(elem, index)) // TODO or: location.withIndex(index)
    }
  }

}