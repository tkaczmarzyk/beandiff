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
package org.beandiff.core

import org.beandiff.core.model.Path
import org.beandiff.core.model.FieldProperty
import org.beandiff.support.FieldSupport.enrichField
import org.beandiff.support.FieldSupport
import org.beandiff.support.ClassSupport.convert
import org.beandiff.core.model.Property


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

  override def routes(o1: Any, o2: Any) = { // TODO too much responsibility?
    if (o1.getClass == o2.getClass) {
      o1.getClass.fieldsInHierarchy map {
      f =>
        {
          (new FieldProperty(f.getName), (f.getFrom(o1), f.getFrom(o2)))
        }
      }
    } else {
      val o1fields = o1.getClass.fieldsInHierarchyByName
      val o2fields = o2.getClass.fieldsInHierarchyByName
      
      val fieldNames = o1fields.keySet ++ o2fields.keySet
      
      for (fname <- fieldNames)
        yield (o1fields.get(fname), o2fields.get(fname)) match {
          case (Some(f1), Some(f2)) => (Property(fname), (Some(f1.get(o1)), Some(f2.get(o2))))
          case (None, Some(f2)) => (Property(fname), (None, Some(f2.get(o2))))
          case (Some(f1), None) => (Property(fname), (Some(f1.get(o1)), None))
        }
    }
  }
  
  protected def getDeclaredFields(c: Class[_]) = { // TODO get rid of it
    c.getDeclaredFields()
  }
}