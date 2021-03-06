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

import org.beandiff.beans.DescendantJavaBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Property
import org.junit.runner.RunWith
import org.beandiff.core.RoutePlanner.Route
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers


@RunWith(classOf[JUnitRunner])
class FieldRoutePlannerTest extends FunSuite with ShouldMatchers {
  
  val router = new FieldRoutePlanner
  
  
  test("should ignore static fields") {
    val b = new SimpleJavaBean("b", 1)
    
    val routes = router.routes(b, b)
    
    routes should have size 2
  }
  
  test("should find all non-static fields") {
    val b = new SimpleJavaBean("b", 1)
    
    val routes = router.routes(b, b)
    
    routes should contain (Route(Property("name"), Some("b"), Some("b")))
    routes should contain (Route(Property("value"), Some(1), Some(1)))
  }
  
  test("should include fields from supertype") {
    val c = new DescendantJavaBean("c", 1, "x")
    
    val routes = router.routes(c, c)
    
    routes should have size 3
    routes should contain (Route(Property("name"), Some("c"), Some("c")))
    routes should contain (Route(Property("value"), Some(1), Some(1)))
    routes should contain (Route(Property("nickname"), Some("x"), Some("x")))
  }
  
  test("should include fields from right even though they're not present on left") {
    val base = new SimpleJavaBean("base", 1)
    val derived = new DescendantJavaBean("derived", 1, "d")
    
    router.routes(base, derived) should have size 3
  }
  
  test("should correctly retrieve routes from instances of different types") {
    val o1 = new SimpleJavaBean("a", 7)
    val o2 = new Object() {
      private val name = "b"
      private val id = 99
    }
    
    val routes = router.routes(o1, o2)
    
    routes should have size 3
    routes should contain (Route(Property("name"), Some("a"), Some("b")))
    routes should contain (Route(Property("value"), Some(7), None))
    routes should contain (Route(Property("id"), None, Some(99)))
  }
}