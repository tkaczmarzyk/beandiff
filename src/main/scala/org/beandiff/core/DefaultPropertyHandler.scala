package org.beandiff.core

class DefaultPropertyHandler extends PropertyHandler {

  def handle(val1: Any, val2: Any, current: Path, walker: ObjectWalker, callback: (Path, Any, Any, Boolean) => Unit): Unit = {
    walker.walk(current, val1, val2)(callback)
  }
}