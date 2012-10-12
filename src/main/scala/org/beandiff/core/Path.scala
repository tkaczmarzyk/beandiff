package org.beandiff.core

import java.lang.reflect.Field

object Path {
  
  val NameSeparator = '.'

  def of(path: String): Path = {
    of(path.split(NameSeparator))
  }

  def of(path: Iterable[String]): Path = {
    if (path.size == 1)
      new Path(Property.of(path.head))
    else
      new Path(Property.of(path.head), of(path.tail))
  }
}

class Path(val head: Property, val tail: Path) {

  def this(head: Property) = {
    this(head, null)
  }

  def depth: Int = {
    if (tail == null) 1
    else 1 + tail.depth
  }
  
  def withIndex(i: Int): Path = {
    if (tail == null)
      new Path(new Property(head.name, i))
    else
      new Path(head, tail.withIndex(i))
  }
  
  def step(p: Property): Path = {
    if (tail == null)
      new Path(head, new Path(p, null))
    else
      new Path(head, tail.step(p))
  }
  
  def last: Property = {
    if (tail == null) 
      head
    else tail.last
  }
}