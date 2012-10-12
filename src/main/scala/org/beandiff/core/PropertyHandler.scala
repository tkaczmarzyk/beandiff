package org.beandiff.core

trait PropertyHandler {

  def handle(val1: Any, val2: Any, current: Path, walker: ObjectWalker, callback: (Path, Any, Any, Boolean) => Unit): Unit
}