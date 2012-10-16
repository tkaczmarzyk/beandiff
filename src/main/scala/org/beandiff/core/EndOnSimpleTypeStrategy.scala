/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 * 
 * This file is part of BeanDiff.
 * 
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
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

object EndOnSimpleTypeStrategy extends DescendingStrategy {

  private val leafClasses = List(classOf[String], classOf[Boolean],
    classOf[Int], classOf[Integer], classOf[Long], classOf[Double], classOf[Character],
    classOf[java.lang.Long], classOf[java.lang.Float], classOf[java.lang.Double],
    classOf[java.lang.Boolean], classOf[java.lang.Byte], classOf[java.lang.Enum[_]])

  def shouldProceed(obj1: Any, ojb2: Any): Boolean = {
    !leafClasses.exists(_.isAssignableFrom(obj1.getClass))
  }
}