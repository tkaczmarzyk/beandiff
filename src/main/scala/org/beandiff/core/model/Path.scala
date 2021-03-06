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

import org.beandiff.support.ClassDictionary


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

  def apply(pathStr: String) = Path.of(pathStr)
  
  def apply(props: Property*): Path = {
    new PathImpl(Vector(props.dropWhile(_ == Self):_*)) // TODO it's kind of workaround
    //apply(properties)
  }
  
//  def apply(props: Seq[Property]): Path = {
//    
//  }
  
  val EmptyPath: Path = new PathImpl(Vector())
}

abstract class Path {

  def depth: Int

  def withIndex(i: Int): Path = step(new IndexProperty(i))

  def get(o: Any): Option[Any]

  def step(p: Property): Path
  def stepBack: Path
  
  def props: Seq[Property]
  
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
  
  def mkString: String
  
  def mkString(toStrDict: ClassDictionary[(Any => String)]): String
}