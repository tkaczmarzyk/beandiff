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
package org.beandiff

import org.beandiff.BeanDiff.diff
import org.beandiff.TypeDefs._
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Path
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.CollectionBean


@RunWith(classOf[JUnitRunner])
class BeanDiffTransformTest extends FunSuite with ShouldMatchers {

  test("should update when 2 sets are on the path") {
    val col1 = new CollectionBean(JSet(new SimpleJavaBean("Donald", 1)))
    val bean1 = new ParentBean("bean", JSet(col1))

    val col2 = new CollectionBean(JSet(new SimpleJavaBean("Sknerus", 1)))
    val bean2 = new ParentBean("bean", JSet(col2))

    diff(bean1, bean2).transformTarget()
    
    val updatedVal = bean1.getChild().asInstanceOf[JSet].iterator().next().asInstanceOf[CollectionBean[JSet]].collection.iterator().next().asInstanceOf[SimpleJavaBean].getName() // FIXME nice way to get it
    updatedVal should be ===  "Sknerus"
  }

  test("should update a simple property") {
    val bean1 = new SimpleJavaBean("bean", 1)
    val bean2 = new SimpleJavaBean("bean", 2)

    diff(bean1, bean2).transformTarget()

    bean1.getValue() should be === 2
  }

  test("should update a property of a nested bean") {
    val parent1 = new ParentBean("parent", new SimpleJavaBean("bart", 1))
    val parent2 = new ParentBean("parent", new SimpleJavaBean("lisa", 1))

    diff(parent1, parent2).transformTarget()

    Path("child.name").value(parent1) should be === "lisa"
  }

  test("should insert an element to a list") {
    val list1 = JList("a", "c")
    val list2 = JList("a", "b", "c")

    diff(list1, list2).transformTarget()

    list1 should be === JList("a", "b", "c")
  }

  test("should add an element to a set") {
    val set1 = JSet("a", "c")
    val set2 = JSet("a", "b", "c")

    diff(set1, set2).transformTarget()

    set1 should be === JSet("a", "b", "c")
  }

  test("should remove an element from a list") {
    val list1 = JList("a", "b", "c")
    val list2 = JList("a", "c")

    diff(list1, list2).transformTarget()

    list1 should be === JList("a", "c")
  }

  test("should remove an element from a set") {
    val set1 = JSet("a", "b", "c")
    val set2 = JSet("a", "c")

    diff(set1, set2).transformTarget()

    set1 should be === JSet("a", "c")
  }
  
  // TODO
  ignore("should add element to a set within a set") { // "A feint within a feint within a feint..."
    val set1 = JSet(JSet("a", "b"))
    val set2 = JSet(JSet("a", "b", "c"))
    val d = diff(set1, set2)
    diff(set1, set2).transformTarget()
    
    set1 should be === JSet(JSet("a", "b", "c"))
  }
}