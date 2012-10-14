package org.beandiff.core

class LeafDiff(o1: Any, o2: Any) extends Diff(o1, o2, null) {

  override val hasDifference = true
}