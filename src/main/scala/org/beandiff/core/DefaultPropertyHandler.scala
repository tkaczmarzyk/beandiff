package org.beandiff.core

class DefaultPropertyHandler extends PropertyHandler {

  def handle(current: Path, o1: Any, o2: Any, walker: ObjectWalker): Unit = {
    o1.getClass.getDeclaredFields foreach {
        f =>
        f.setAccessible(true)

        val val1 = f.get(o1)
        val val2 = f.get(o2)
        val path = current.step(new FieldProperty(f.getName))

        walker.walk(path, val1, val2)
    }
  }
}