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
package org.beandiff

import java.util.ArrayList
import org.beandiff.beans.CollectionBean
import org.beandiff.BeanDiff._
import org.beandiff.beans.ParentBean
import org.perf4j.StopWatch
import org.beandiff.beans.SimpleJavaBean

object PerformanceCheck extends App {

  val bigBean = new CollectionBean[ParentBean](new ArrayList)
  for (i <- 0 until 1024) {
    val parentBean = new ParentBean(i.toString)
    val colBean = new CollectionBean[SimpleJavaBean](new ArrayList);
    parentBean.setChild(new ParentBean(i + " child"), colBean)
    for (j <- 0 until 1024) {
      colBean.collection.add(new SimpleJavaBean(i + "_" + j, i * j))
    }
    bigBean.collection.add(parentBean)
  }

  val clock = new StopWatch

  clock.start()
  val d = diff(bigBean, bigBean)
  clock.stop()

  assert(!d.hasDifference)

  println(clock.getElapsedTime() / 1000.0)
}