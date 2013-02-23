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
package org.beandiff.equality

import org.beandiff.support.AnnotationUtil.enrich
import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method

class AnnotationEqualityInvestigator[A <: Annotation](
  annoClass: Class[A])(
    implicit private val m: Manifest[A]) extends BaseEqInvestigator {

  override def objsAreEqual(o1: Any, o2: Any) = {
    val fields = o1.annotatedFields[A]
    val methods = o1.annotatedMethods[A]
    if (fields.isEmpty && methods.isEmpty) false
    else {
      fields.forall((f: Field) => f.get(o1) == f.get(o2)) &&
        methods.forall((m: Method) => m.invoke(o1) == m.invoke(o2))
    }
  }
}