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
package org.beandiff.support

import org.beandiff.support.ClassSupport.convert
import org.beandiff.support.ClassDictionary.Entry

object ClassDictionary {
  type Entry[T] = (Class[_], T)
}

class ClassDictionary[T](
  final val defaultValue: T,
  private val content: Map[Class[_], T]) extends Equals {

  def this(defaultValue: T) = {
    this(defaultValue, Map[Class[_], T]())
  }

  def this() = this(null.asInstanceOf[T])

  def this(defaultValue: T, entries: Iterable[Entry[T]]) = {
    this(defaultValue, entries.toMap)
  }

  def this(defaultValue: T, values: Entry[T]*) = {
    this(defaultValue, values.toMap)
  }

  def withDefault(defaultValue: T): ClassDictionary[T] = {
    new ClassDictionary[T](defaultValue, content)
  }

  def withEntry[U >: T](entry: Entry[U]) = {
    new ClassDictionary(defaultValue, content + entry)
  }

  def withEntries[U >: T](entries: Iterable[Entry[U]]) = {
    new ClassDictionary(defaultValue, content ++ entries.toMap)
  }

  def apply(c: Class[_]): T = {
    if (content.contains(c))
      content(c)
    else { // TODO candidate for performance optimization
      val supportedSuperTypes = c.allSuperTypes.filter(content.contains(_))
      if (!supportedSuperTypes.isEmpty)
        content(supportedSuperTypes.head)
      else
        defaultValue
    }
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[org.beandiff.support.ClassDictionary[T]]
  }

  override def equals(other: Any) = {
    other match {
      case that: ClassDictionary[T] => that.canEqual( this) && defaultValue == that.defaultValue && content == that.content
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime * (prime + defaultValue.hashCode) + content.hashCode
  }

}