package org.beandiff.core

trait RoutePlanner {

  def guide(current: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit
}