/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
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

object Path {

  final val FieldSeparator = "."

  /**
   * Parses path from string input. Example argument:
   *
   * {@code nicknames[0].shortVersion}
   *
   * @param pathStr string representation to be parsed
   * @return an instance of {@link Path}
   */
  def of(pathStr: String): Path = {
    new PathParser().parsePath(pathStr)
  }

}

class Path(val head: Property, val tail: Path) {

  def this(head: Property) = {
    this(head, EmptyPath)
  }

  def depth: Int = {
    { if (head != null) 1 else 0 } + tail.depth //TODO add ThisProperty class, avoid null
  }

  def withIndex(i: Int): Path = step(new IndexProperty(i))

  def value(o: Any): Any = {
    if (tail == null)
      head.value(o)
    else
      tail.value(head.value(o))
  }

  def stepBack: Path = {
    if (tail == null)
      EmptyPath
    else
      new Path(head, tail.stepBack)
  }

  def step(p: Property): Path = {
    if (tail == null)
      new Path(head, new Path(p, null))
    else
      new Path(head, tail.step(p))
  }

  def ++(other: Path): Path = {
    if (other == EmptyPath)
      this
    else this.step(other.head) ++ other.tail
  }

  def step(props: List[Property]): Path = {
    if (props.isEmpty)
      this
    else
      this.step(props.head).step(props.tail)
  }

  def last: Property = {
    if (tail == null || tail.depth == 0)
      head
    else tail.last
  }

  override def equals(o: Any): Boolean = {
    o match {
      case that: Path => this.head == that.head && this.tail == that.tail
      case _ => false
    }
  }

  override def toString() = {
    head.toString + {
      if (tail != null)
        if (tail.head.isInstanceOf[FieldProperty]) // FIXME avoid type check
          Path.FieldSeparator + tail.toString
        else
          tail.toString
      else ""
    }
  }
}