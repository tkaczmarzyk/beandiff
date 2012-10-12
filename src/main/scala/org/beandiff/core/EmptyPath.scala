package org.beandiff.core

object EmptyPath extends Path(null, null) {
  
  override val depth = 0
  
  override def step(p: Property) = new Path(p, null)
}