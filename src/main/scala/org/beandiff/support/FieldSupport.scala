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

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.beandiff.support.ClassSupport.RichClass


object FieldSupport {

  implicit class RichField(val f: Field) extends AnyVal {

    def isStatic = Modifier.isStatic(f.getModifiers())

    def getFrom(src: Any): Option[Any] = {
      if (src.getClass.hasField(f.getName))
        Some(f.get(src))
      else
        None
    }
  }
}