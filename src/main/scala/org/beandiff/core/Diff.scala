package org.beandiff.core

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

// TODO reduce mutability
class Diff(
    val o1: Any,
    val o2: Any,
    val diffs: Map[Property, Diff]) { // FIXME improve encapsulation

  
  def this(o1: Any, o2: Any) = this(o1, o2, new HashMap)

  
  def update(p: Path, d: Diff): Unit = {
    if (p.depth == 1) {
      diffs += (p.head -> d)
    } else {
      if (!diffs.contains(p.head)) {
        diffs += (p.head -> new Diff(p.stepBack.value(o1), p.stepBack.value(o2)))
      }
      diffs(p.head).update(p.tail, d)
    }
  }
  
  def hasDifference() = !diffs.isEmpty
  
  def hasDifference(path: String): Boolean = hasDifference(Path.of(path))
  
  def hasDifference(p: Path): Boolean = {
    if (!diffs.contains(p.head))
      false
    else
      (p.depth == 1) || diffs(p.head).hasDifference(p.tail)
  }
  
  override def toString() = "Diff[" + o1 + ", " + o2 + "]"
}