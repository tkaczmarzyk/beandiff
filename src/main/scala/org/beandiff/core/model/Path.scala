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

  def apply(properties: Property*): Path = {
    new PathImpl(Vector(properties.dropWhile(_ == Self):_*)) // TODO it's kind of workaround
  }
  
  val EmptyPath = new PathImpl(Vector())
}

abstract class Path {

  def depth: Int

  def withIndex(i: Int): Path = step(new IndexProperty(i))

  def value(o: Any): Any

  def step(p: Property): Path

  def props: Iterable[Property]
  
  def ++(other: Path): Path

  def step(props: List[Property]): Path = {
    if (props.isEmpty)
      this
    else
      this.step(props.head).step(props.tail)
  }

  def head: Property
  
  def last: Property
  
  def tail: Path
  
  def isPrefixOf(other: Path): Boolean
}