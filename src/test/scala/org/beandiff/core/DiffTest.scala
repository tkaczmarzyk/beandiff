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

import org.beandiff.BeanDiff
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.DiffImpl
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.core.model.NewValue
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.FieldProperty
import org.beandiff.core.model.Property

@RunWith(classOf[JUnitRunner])
class DiffTest extends FunSuite with ShouldMatchers {

  val simpleBean1 = new SimpleJavaBean("aa", 1)
  val simpleDiff = BeanDiff.diff(simpleBean1, new SimpleJavaBean("bb", 1))
  
  
  test("should add change at the path") {
    val diff = new DiffImpl(EmptyPath, null, Map())
    val updated = diff.withChange(Path.of("aa.bb"), new NewValue(new FieldProperty("bb"), ":(", ":)"))
    
    assert(updated.leafChanges.exists(_._1 == Path.of("aa.bb")))
  }
  
  test("there should be no difference in simple property after transformation") {
    simpleDiff.transformTarget()
    simpleBean1.getName() should be === "bb"
  }
}