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

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import org.beandiff.support.ObjectSupport.convert
import java.lang.reflect.Method

object AnnotationUtil {
  implicit def enrich(o: Any): AnnotationUtil = new AnnotationUtil(o)
}

class AnnotationUtil(target: Any) {

  def annotatedFields[A <:Annotation](implicit m: Manifest[A]): Traversable[Field] = {
    val anno = m.erasure.asInstanceOf[Class[Annotation]]
    target.allClasses.foldLeft(List[Field]())(
        (acc: List[Field], clazz: Class[_]) => acc ++ annotatedFields(anno, clazz))
  }
  
  private def annotatedFields[A <:Annotation, T](anno: Class[A], clazz: Class[T]): Traversable[Field] = {
    val annotated = clazz.getDeclaredFields().filter(_.getAnnotation(anno) != null)
    annotated.map((f: Field) => {
      f.setAccessible(true)
      f
    })
  }
  
  def annotatedMethods[A <:Annotation](implicit m: Manifest[A]): Traversable[Method] = {
    val anno = m.erasure.asInstanceOf[Class[Annotation]]
    target.allClasses.foldLeft(List[Method]())(
        (acc: List[Method], clazz: Class[_]) => acc ++ annotatedMethods(anno, clazz))
  }
  
  private def annotatedMethods[A <:Annotation, T](anno: Class[A], clazz: Class[T]): Traversable[Method] = {
    val annotated = clazz.getDeclaredMethods().filter(_.getAnnotation(anno) != null)
    val paramlessNonVoid = annotated.filter((m: Method) => {
      m.getParameterTypes().isEmpty && m.getReturnType() != null
    })
    paramlessNonVoid.map((m: Method) => {
      m.setAccessible(true)
      m
    })
  }
}