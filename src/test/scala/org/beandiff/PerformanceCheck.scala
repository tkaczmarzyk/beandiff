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
import java.util.Collection
import java.util.Collections
import java.util.List
import org.beandiff.BeanDiff.diff
import org.beandiff.beans.CollectionBean
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.perf4j.StopWatch
import java.util.HashSet


object PerformanceCheck extends App {

  val numElems = 700
  val bigBean1, bigBean2 = new CollectionBean[ParentBean](new ArrayList)
  for (bigBean <- Vector(bigBean1, bigBean2)) {
    for (i <- 0 until numElems) {
      val parentBean = new ParentBean(i.toString)
      val colBean = new CollectionBean[SimpleJavaBean](new ArrayList);
      parentBean.setChild(new ParentBean(i + " child", colBean))
      for (j <- 0 until numElems) {
        colBean.collection.add(new SimpleJavaBean(i + "_" + j, i * j))
      }
      bigBean.collection.add(parentBean)
    }
  }

  val clock = new StopWatch
  val iters = 10
  
  clock.start()
  for (i <- 1 to iters) {
    assert(!diff(bigBean1, bigBean2).hasDifference)
  }
  clock.stop()

  println(clock.getElapsedTime() / 1000.0 / iters)
}