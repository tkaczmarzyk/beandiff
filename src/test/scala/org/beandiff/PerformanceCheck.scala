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