package org.beandiff.core

class FieldRoutePlanner extends RoutePlanner {

  def guide(current: Path, o1: Any, o2: Any, walker: ObjectWalker): Unit = {
    getDeclaredFields(o1.getClass) foreach {
        f =>
        f.setAccessible(true)

        val val1 = f.get(o1)
        val val2 = f.get(o2)
        val path = current.step(new FieldProperty(f.getName))

        walker.walk(path, val1, val2)
    }
  }
  
  protected def getDeclaredFields(c: Class[_]) =
    c.getDeclaredFields()
}