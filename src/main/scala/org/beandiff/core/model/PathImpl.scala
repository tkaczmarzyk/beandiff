/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.beandiff.core.model

final class PathImpl(
  val props: Vector[Property]) extends Path with Equals {

  def this(head: Property) = {
    this(Vector(head))
  }

  override def depth: Int = props.size

  override def withIndex(i: Int): Path = step(new IndexProperty(i))

  override def value(o: Any): Any = {
    if (props.isEmpty)
      o
    else
      tail.value(props.head.value(o))
  }

  override def head = 
    if (props.isEmpty) Self else props.head

  override def tail = new PathImpl(props.tail)

  override def step(p: Property): Path = { // TODO avoid typecheck
    if (p == Self)
      this
    else
      new PathImpl(props :+ p)
  }
  
  override def stepBack = new PathImpl(props.init)

  override def ++(other: Path): Path =
    new PathImpl(props ++ other.props)

  override def last: Property = 
    if (props.isEmpty) Self else props.last

  override def isPrefixOf(other: Path) = {
      if (depth == 0)
        true
      else if (head == other.head)
        tail.isPrefixOf(other.tail)
      else
        false
  }
    
  override def toString() = { // TODO
    if (depth == 0)
      ""
    else
      head.toString + {
        if (tail.depth > 0)
          if (tail.head.isInstanceOf[FieldProperty]) // FIXME avoid type check
            Path.FieldSeparator + tail.toString
          else
            tail.toString
        else ""
      }
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[org.beandiff.core.model.PathImpl]
  }

  override def equals(other: Any) = {
    other match {
      case that: org.beandiff.core.model.PathImpl => that.canEqual(PathImpl.this) && props == that.props
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + props.hashCode
  }

}