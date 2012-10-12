package org.beandiff.core

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map


class Diff(val diffs: Map[Property, Diff]) {

  def this() = this(new HashMap)

  
  def update(p: Path, d: Diff): Unit = {
    if (p.depth == 1) {
      diffs += (p.head -> d)
    } else {
      if (!diffs.contains(p.head)) {
        diffs += (p.head -> new Diff())
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
}