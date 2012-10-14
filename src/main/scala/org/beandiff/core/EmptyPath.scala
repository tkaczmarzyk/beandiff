package org.beandiff.core

//TODO reorganize path hierarchy
object EmptyPath extends Path(null, null) {
  
  override val depth = 0
  
  override def step(p: Property) = new Path(p, null)
  
  override def value(o: Any): Any = o
}