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
package org.beandiff.core

class FieldRoutePlanner extends RoutePlanner {

  def guide(current: Path, o1: Any, o2: Any, walker: ObjectWalker): Unit = {
    getDeclaredFields(o1.getClass) foreach {
        f =>
        f.setAccessible(true)

        val val1 = f.get(o1)
        val val2 = f.get(o2)
        val path = current.step(new FieldProperty(f.getName))

        walker.walk(path, val1, val2)
    }
  }
  
  protected def getDeclaredFields(c: Class[_]) =
    c.getDeclaredFields()
}