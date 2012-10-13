package org.beandiff.core

trait RoutePlanner {

  def handle(current: Path, val1: Any, val2: Any, walker: ObjectWalker): Unit
}