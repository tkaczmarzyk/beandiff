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
import org.beandiff.DiffEngineBuilder._
import org.beandiff.TestDefs.EverythingIsEntityWithNameId
import org.beandiff.TypeDefs._
import org.beandiff.beans.CollectionBean
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.beans.SimpleJavaBean._
import org.beandiff.beans.Simpsons
import org.beandiff.core.DiffEngineCoordinator
import org.beandiff.core.LcsDiffEngine
import org.beandiff.core.model.Path
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.SelectiveEqualityInvestigator
import org.beandiff.lcs.NaiveLcsCalc
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.beandiff.test.ObjectTestSupport.convert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.beandiff.beans.scala.Parent
import org.beandiff.beans.scala.Child
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class BeanDiffTransformTest extends FunSuite with ShouldMatchers {

  private trait Beans {
    val a1 = new SimpleJavaBean("a", 1)
    val a2 = new SimpleJavaBean("a", 2)
    val b1 = new SimpleJavaBean("b", 1)
    val c1 = new SimpleJavaBean("c", 1)
    val x1 = new SimpleJavaBean("x", 1)
  }

  test("should update a simple property") {
    val bean1 = new SimpleJavaBean("bean", 1)
    val bean2 = new SimpleJavaBean("bean", 2)

    diff(bean1, bean2).transformTarget()

    bean1.getValue() should be === 2
  }

  test("should update when 2 sets are on the path") {
    val col1 = new CollectionBean(JSet(new SimpleJavaBean("Donald", 1)))
    val bean1 = new ParentBean("bean", JSet(col1))

    val col2 = new CollectionBean(JSet(new SimpleJavaBean("Sknerus", 1)))
    val bean2 = new ParentBean("bean", JSet(col2))

    diff(bean1, bean2).transformTarget()

    val updatedVal = bean1("child").firstElem("collection").firstElem.get("name")
    updatedVal should be === "Sknerus"
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

  test("should add element to a set within a set") { // "A feint within a feint within a feint..."
    val set1 = JSet(JSet("a", "b"))
    val set2 = JSet(JSet("a", "b", "c"))

    diff(set1, set2).transformTarget()

    // {{ // TODO temporary assertions
    set1 should have size 1
    val set1elem = set1.iterator().next()
    set1elem should be === JSet("a", "b", "c")
    //}}
    //    set1 should be === JSet(JSet("a", "b", "c")) // TODO fails. ivestigate
  }

  test("should remove an element from a set within a set") {
    val set1 = JSet(JSet("a", "b"))
    val set2 = JSet(JSet("b"))

    diff(set1, set2).transformTarget()

    // {{ // TODO temporary assertions
    set1 should have size 1
    val set1elem = set1.iterator().next()
    set1elem should be === JSet("b")
    //}}
    // set1 should be === JSet(JSet("b")) // TODO fails. ivestigate
  }

  test("should replace element of a set within a set") {
    val set1 = JSet(JSet("a"))
    val set2 = JSet(JSet("b"))

    diff(set1, set2).transformTarget()

    // {{ // TODO temporary assertions
    set1 should have size 1
    val set1elem = set1.iterator().next()
    set1elem should be === JSet("b")
    //}}
    // set1 should be === JSet(JSet("b")) // TODO fails. ivestigate
  }

  test("should replace an element of a list") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("A", "b", "c")

    diff(l1, l2).transformTarget()
    l1 should be === JList("A", "b", "c")
  }

  test("should update property of an element of the list") {
    new Beans {
      val l1 = JList(a1, b1, c1)
      val l2 = JList(a2, b1, c1)

      diff(l1, l2).transformTarget()
      l1 should be === JList(a1, b1, c1)
      a1.getName() should be === "a"
      a1.getValue() should be === 2
    }
  }

  test("should update property of an element of the set") {
    new Beans {
      val l1 = JSet(orderByName, a1, b1, c1)
      val l2 = JSet(orderByName, a2, b1, c1)

      diff(l1, l2).transformTarget()
      l1 should be === JSet(orderByName, a1, b1, c1)
      a1.getName() should be === "a"
      a1.getValue() should be === 2
    }
  }

  test("should update property of the modified element even though other one is to be inserted ahead of it") {
    new Beans {
      val l1 = JList(a1, b1, c1)
      val l2 = JList(x1, a2, b1, c1)

      val engine = new LcsDiffEngine(BeanDiff.diffEngine().asInstanceOf[DiffEngineCoordinator],
        EverythingIsEntityWithNameId, new NaiveLcsCalc()) // TODO simplify creation

      engine.calculateDiff(l1, l2).transformTarget()
      l1 should be === JList(x1, a1, b1, c1)
      a1.getName() should be === "a"
      a1.getValue() should be === 2
    }
  }

  test("should add sequence of elements to a list") {
    val l1 = JList("c", "d")
    val l2 = JList("a", "b", "c", "d")

    diff(l1, l2).transformTarget()
    l1 should be === JList("a", "b", "c", "d")
  }

  test("should add multiple elements to a list") {
    val l1 = JList("b", "d")
    val l2 = JList("a", "b", "c", "d", "e")

    diff(l1, l2).transformTarget()
    l1 should be === JList("a", "b", "c", "d", "e")
  }

  test("should correctly handle insert next to delete") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("a", "X", "d")

    diff(l1, l2).transformTarget()
    l1 should be === JList("a", "X", "d")
  }

  test("should correctly perform delete head with insert last") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "c", "d", "e")

    diff(l1, l2).transformTarget()
    l1 should be === JList("b", "c", "d", "e")
  }

  test("should correctly perform insert as first with delete last") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("X", "a", "b", "c")

    diff(l1, l2).transformTarget()
    l1 should be === JList("X", "a", "b", "c")
  }

  test("should delete sequence of elements") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("a", "d")

    diff(l1, l2).transformTarget()
    l1 should be === JList("a", "d")
  }

  test("should delete multiple elements") {
    val l1 = JList("a", "b", "c", "d", "e")
    val l2 = JList("a", "c", "e")

    diff(l1, l2).transformTarget()
    l1 should be === JList("a", "c", "e")
  }

  test("should detect that an element has been modified even though its id is unchanged") {
    new Beans {
      val l1 = JList(a1, b1, c1)
      val l2 = JList(a2, b1, c1)

      val engine = new LcsDiffEngine(BeanDiff.diffEngine().asInstanceOf[DiffEngineCoordinator],
        EverythingIsEntityWithNameId, new NaiveLcsCalc()) // TODO simplify creation

      engine.calculateDiff(l1, l2).transformTarget()

      l1 should be === JList(a1, b1, c1)
      a1.getName() should be === "a"
      a1.getValue() should be === 2
    }
  }

  test("should correctly transform list within a set") {
    val s1 = JSet(JList("a", "b", "c"))
    val s2 = JSet(JList("a", "x", "b"))

    diff(s1, s2).transformTarget()

    //s1 should be === JSet(JList("a", "x", "b")) FIXME: fails, investigate
    s1 should have size 1
    s1.getClass() should be === JSet().getClass()
    s1.firstElem() should be === JList("a", "x", "b")
  }

  test("should shift an element in a list") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("a", "c", "b")

    diff(l1, l2).transformTarget()

    l1 should be === JList("a", "c", "b")
  }

  test("should perform multiple changes in a list (3)") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("c", "b", "a")

    diff(l1, l2).transformTarget()

    l1 should be === JList("c", "b", "a")
  }

  test("should perform multiple changes in a list") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("c", "x", "a")

    diff(l1, l2).transformTarget()

    l1 should be === JList("c", "x", "a")
  }

  test("should perform shift and new-value on a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("x", "a", "c", "d")

    diff(l1, l2).transformTarget()

    l1 should be === JList("x", "a", "c", "d")
  }

  test("should perform shift and insertion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "c", "d", "a", "x")

    diff(l1, l2).transformTarget()

    l1 should be === JList("b", "c", "d", "a", "x")
  }

  test("should perform shift and deletion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "c", "a")

    diff(l1, l2).transformTarget()

    l1 should be === JList("b", "c", "a")
  }

  test("should perform shift and deletion in a list (2)") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "d", "a")

    diff(l1, l2).transformTarget()
    l1 should be === JList("b", "d", "a")
  }

  test("should perform multiple changes in a list (2)") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "d", "a")

    diff(l1, l2).transformTarget()
    l1 should be === JList("b", "d", "a")
  }

  test("should perfrom new-value and insertion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("x", "b", "c", "d", "a")

    diff(l1, l2).transformTarget()
    l1 should be === JList("x", "b", "c", "d", "a")
  }

  test("should perform changes on the shifted element") {
    new Simpsons {
      val engine = aDiffEngine.withEntity[SimpleJavaBean]("name")

      val l1 = JList(maggie, lisa, bart)
      val l2 = JList(lisa, bart, maggie2)

      engine.calculateDiff(l1, l2).transformTarget()

      l1 should be === JList(lisa, bart, maggie)
      maggie.getValue() should be === 2
    }
  }

  ignore("todo") {
    val a = JSet(Parent("b", JList(Child("c", 3))))
    val b = JSet(Parent("b", JList()), Parent("b", JList(Child("c", -1))))

    val d = diff(a, b)
    d.transformTarget()

    assert(a === b)
  }

  test("should be able to transform a list to any of its permutations") {
    val list = List("a", "b", "c", "d", "e")
    val l1 = JList(list: _*)

    for (perm <- list.permutations) {
      val l2 = JList(perm: _*)

      diff(l1, l2).transformTarget()

      assert(l1 === l2, "Error for permutation: " + perm)
    }
  }
}