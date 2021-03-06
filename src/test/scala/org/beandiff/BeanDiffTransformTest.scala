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
import org.beandiff.BeanDiff.diffEngine
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
import org.beandiff.core.model.change.ChangeOrdering
import org.beandiff.beans.DescendantJavaBean
import org.beandiff.test.JMap

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

    Path("child.name").get(parent1) should be === Some("lisa")
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

    set1 should be === JSet(JSet("a", "b", "c"))
  }

  test("should remove an element from a set within a set") {
    val set1 = JSet(JSet("a", "b"))
    val set2 = JSet(JSet("b"))

    diff(set1, set2).transformTarget()

    set1 should be === JSet(JSet("b"))
  }

  test("should replace element of a set within a set") {
    val set1 = JSet(JSet("a"))
    val set2 = JSet(JSet("b"))

    diff(set1, set2).transformTarget()

    set1 should be === JSet(JSet("b"))
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

      val engine = new LcsDiffEngine(BeanDiff.diffEngine().build().asInstanceOf[DiffEngineCoordinator],
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

      val engine = new LcsDiffEngine(BeanDiff.diffEngine().build().asInstanceOf[DiffEngineCoordinator],
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

    s1 should be === JSet(JList("a", "x", "b"))
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

  test("should perform both self and deep changes on a set") {
    val a = JSet(Parent("b", JList(Child("c", 3))))
    val b = JSet(Parent("b", JList()), Parent("b", JList(Child("c", -1))))

    diff(a, b).transformTarget()

    a should be === JSet(Parent("b", JList()), Parent("b", JList(Child("c", -1))))
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

  test("should correctly transform lists of entities with sublists") {
    val l1 = JList(Parent("a", JList(Child("d", -1), Child("d", -3))))
    val l2 = JList(Parent("a", JList()), Parent("d", JList()))

    aDiffEngine.withEntity[Parent]("name").calculateDiff(l1, l2).transformTarget()

    assert(l1 === l2)
  }

  test("should correctly transform into set with multiple versions of the same entity") {
    val s1 = JSet(Parent("c", JList(Child("b", 5))))
    val s2 = JSet(Parent("c", JList()), Parent("c", JList(Child("b", 5))))
    
    aDiffEngine.withEntity[Parent]("name").calculateDiff(s1, s2).transformTarget()
    
    assert(s1 === s2)
  }

  test("should transform entity lists of different lengths") {
    val a = JList(Parent("a", JList(Child("c", 2), Child("d", 3), Child("c", -5), Child("d", -2))))
    val b = JList(Parent("a", JList(Child("b", 2), Child("d", -2), Child("b", 0))),
        Parent("c", JList()))

    aDiffEngine.withEntity[Parent]("name").calculateDiff(a, b).transformTarget()
    assert(a === b)
  }
  
  test("should transfrom lists of entities with sub entities") {
    val a = JList(Parent("a", JList(Child("c", 2), Child("d", 3), Child("c", -5), Child("d", -2))))
    val b = JList(Parent("a", JList(Child("b", 2), Child("d", -2), Child("b", 0))),
        Parent("c", JList()))

    aDiffEngine.withEntity[Parent]("name").withEntity[Child]("name").calculateDiff(a, b).transformTarget()
    assert(a === b)
  }

  test("should transform set when one version of an entity is going to be transformed to a one equal to the version to be removed") {
    val a = JSet(Parent("c", JList(Child("c", -3))), Parent("c", JList())) // FIXME it might depend on the order after to-list transformation, add test with a comparator that would ensure the ordering
    val b = JSet(Parent("c", JList(Child("c", -3))))

    aDiffEngine.withEntity[Parent]("name").calculateDiff(a, b).transformTarget()
    assert(a === b)
  }
  
  test("should transform list of ints into other list of ints") {
    val a = JList(0, -1, -2147483648, -2146959360, -2147483648, -1, -2147483648, 0, -773887957, -996860749, 0, -607992824, 253564682, 987035484)
    val b = JList(-2147483648, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -2146959360, 1, 1, 1, 1, 1, 1, -2147483648, 1, 1, 1, 1)

    diff(a, b).transformTarget()
    assert(a === b)
  }
  
  test("should update field in superclass") {
    val a = new DescendantJavaBean("a", 1, "x")
    val b = new DescendantJavaBean("b", 1, "x")
    
    diff(a, b).transformTarget()
    
    a.getName() should be === "b"
  }
  
  test("should update instance of base class and ignore fields from derived one") {
    val base = new SimpleJavaBean("aaa", 1)
    val derived = new DescendantJavaBean("bbb", 2, "x")
    
    diff(base, derived).transformTarget()
    
    base.getName should be === "bbb"
    base.getValue should be === 2
  }
  
  test("should update instance of derived class but ignore fields not present in bean on the right") {
    val derived = new DescendantJavaBean("bbb", 2, "x")
    val base = new SimpleJavaBean("aaa", 1)
    
    diff(derived, base).transformTarget()
    
    derived.getName should be === "aaa"
    derived.getValue should be === 1
    derived.getNickname should be === "x"
  }
  
  test("should update fields by name even though classes are different") {
    val o1 = new SimpleJavaBean("a", 7)
    val o2 = new Object() {
      private val name = "b"
      private val value = 9
    }
    
    diff(o1, o2).transformTarget()
    
    o1.getName should be === "b"
    o1.getValue should be === 9
  }
  
  test("should change a simple entry in a map") {
    val m1 = JMap("a" -> 1, "b" -> 2)
    val m2 = JMap("a" -> 9, "b" -> 2)

    diff(m1, m2).transformTarget()

    m1 should be === JMap("a" -> 9, "b" -> 2)
  }

  test("should add a new entry to the map") {
    val m1 = JMap("a" -> 1, "b" -> 2)
    val m2 = JMap("a" -> 1, "b" -> 2, "c" -> 3)

    diff(m1, m2).transformTarget()
    
    m1 should be === JMap("a" -> 1, "b" -> 2, "c" -> 3)
  }

  test("should remove an entry from a map") {
    val m1 = JMap("a" -> 1, "b" -> 2, "c" -> 3)
    val m2 = JMap("a" -> 1, "b" -> 2)

    diff(m1, m2).transformTarget()
    
    m1 should be === JMap("a" -> 1, "b" -> 2)
  }
  
  test("should change a null value in the map") {
    val m1: JMap = JMap("a" -> null)
    val m2 = JMap("a" -> 1)
    
    diff(m1, m2).transformTarget()
    
    m1.get("a") should be === 1
  }
  
  test("should change a value in the map to null") {
    val m1 = JMap("a" -> 1)
    val m2 = JMap("a" -> null)
    
    diff(m1, m2).transformTarget()
    
    m1 should be === JMap("a" -> null)
  }
  
  test("should change property of a value in the map") {
    new Beans {
      val m1 = JMap("a" -> a1)
      val m2 = JMap("a" -> a2)
    
      diff(m1, m2).transformTarget()
      
      m1 should be === JMap("a" -> a1)
      a1.getValue should be === 2
    }
  }
  
  test("should replace an entity in the map") {
    new Beans {
      val engine = diffEngine().withEntity[SimpleJavaBean]("value")
      
      val m1 = JMap("a" -> a1)
      val m2 = JMap("a" -> a2)
    
      engine.calculateDiff(m1, m2).transformTarget()
      
      m1 should be === JMap("a" -> a2)
    }
  }
}