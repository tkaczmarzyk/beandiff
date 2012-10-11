package org.beandiff.core

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map


class Diff(val parent: Diff, val name: String) {

  val children: Map[String, Diff] = new HashMap[String, Diff]()
  
  if (parent != null)
    parent.children.put(this.name, this)
  
  
  def hasDifference(): Boolean = {
    !children.isEmpty
  }
  
  def getChildren() = children
  
  def hasDifference(path: String): Boolean = {
    hasDifference(path.split('.'))
  }
  
  private def hasDifference(path: Array[String]): Boolean = {
    if (path.isEmpty) true
    else if (children.contains(path.head))
      children(path.head).hasDifference(path.tail)
    else false
  }
}