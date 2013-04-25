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

import java.io.PrintWriter
import java.io.StringWriter
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import org.beandiff.BeanDiff.diffEngine
import org.beandiff.BeanDiff.diff
import org.beandiff.BeanDiff.printDiff
import org.beandiff.DiffEngineBuilder.builder2engine
import org.beandiff.beans.CollectionBean
import org.beandiff.beans.DescendantJavaBean
import org.beandiff.beans.NamedBean
import org.beandiff.beans.Node
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.beans.Simpsons
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Path
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.Property
import org.beandiff.core.model.change.Addition
import org.beandiff.core.model.change.Change
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Removal
import org.beandiff.core.model.change.Shift
import org.beandiff.test.BeanDiffMatchers.haveChange
import org.beandiff.test.BeanDiffMatchers.haveDeletionAt
import org.beandiff.test.BeanDiffMatchers.haveDifference
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.beandiff.core.model.change.Addition
import org.beandiff.test.JMap
import org.beandiff.core.model.change.Association
import org.beandiff.core.model.change.KeyRemoval
import org.beandiff.core.model.Self
import org.beandiff.core.model.ElementProperty
import org.beandiff.core.model.KeyProperty


@RunWith(classOf[JUnitRunner])
class BeanDiffTest extends FunSuite with ShouldMatchers {

  trait SimpleBeans {
    val a1a = new SimpleJavaBean("a", 1)
    val a1b = new SimpleJavaBean("a", 1)
    val A1 = new SimpleJavaBean("A", 1)
    val b1 = new SimpleJavaBean("b", 1)
  }

  trait NestedBeans {
    val child1 = new SimpleJavaBean("bart", 10)
    val parent1 = new ParentBean("homer", child1)
    val parent1clone = new ParentBean("homer", new SimpleJavaBean("bart", 10))

    val child2 = new SimpleJavaBean("lisa", 8)
    val parent2 = new ParentBean("homer", child2)
  }

  trait CollectionBeans {
    val abc = CollectionBean.listBean("a", "b", "c")
    val abd = CollectionBean.listBean("a", "b", "d")

    val beans1 = CollectionBean.listBean(new SimpleJavaBean("aaa", 1), new SimpleJavaBean("bbb", 2))
    val beans2 = CollectionBean.listBean(new SimpleJavaBean("aaa", 1), new SimpleJavaBean("bbb", 3))
  }

  trait Collections {
    val jList1 = Arrays.asList("aaa", "bbb", "ccc")
    val jList2 = Arrays.asList("111", "bbb", "ccc")
    val jList3 = Arrays.asList("xxx", "bbb", "ccc")

    val sList1 = List("aaa", "bbb", "ccc")
    val sList2 = List("111", "bbb", "ccc")

    val jSet1 = new HashSet(Arrays.asList("aaa", "bbb"))
    val jSet2 = new HashSet(Arrays.asList("aaa", "bbb"))
    val jSet3 = new HashSet(Arrays.asList("bbb", "ccc"))
  }

  // TODO
  ignore("should detect differnces in scala lists of strings") {
    new Collections {
      val d = diff(sList1, sList2)

      d should haveDifference("[0]")
    }
  }

  test("should compare elements of different type") {
    val o1 = new SimpleJavaBean("name", 1)
    val o2 = new ParentBean("name", 2)

    val d = diff(o1, o2)
//    d should haveDifference("@type") // TODO
    d should haveDifference("child") // ParentBean's property
    d should haveDifference("value") // SimpleJavaBean's property
  }

  test("should handle elements of different types in a list") {
    val a = new ParentBean(new NamedBean("a"))
    val o1 = JList(a, new NamedBean("c"))
    val b = new NamedBean("b")
    val o2 = JList(a, b, new NamedBean("c"))

    diff(o1, o2) should haveChange(new Insertion(b, 1))
    diff(o1, o2).leafChanges should have size 1
  }

  test("should detect 2 new-values") {
    val l1 = JList("a", "b")
    val l2 = JList("x", "y")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(NewValue(Property("[0]"), "a", "x"))
    d should haveChange(NewValue(Property("[1]"), "b", "y"))
  }

  test("should detect shift and insertion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "c", "d", "a", "x")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(Shift("a", 0, 3))
    d should haveChange(Insertion("x", 4))
  }

  test("should detect new-value and insertion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("x", "b", "c", "d", "a")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(Insertion("a", 4))
    d should haveChange(NewValue(Property("[0]"), "a", "x"))
  }

  test("should detect a single back shift") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("d", "a", "b", "c")

    val d = diff(l1, l2)
    d.leafChanges should have size 1
    d should haveChange(Shift("d", 3, 0))
  }

  test("should detect shift and deletion in a list") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "c", "a")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(Shift("a", 0, 2))
    d should haveChange(Deletion("d", 3))
  }

  test("should detect shift and deletion in a list (2)") {
    val l1 = JList("a", "b", "c", "d")
    val l2 = JList("b", "d", "a")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(Shift("a", 0, 2))
    d should haveChange(Deletion("c", 2))
  }

  test("should detect that an element was shifted") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("a", "c", "b")

    val d = diff(l1, l2)
    d.leafChanges should have size 1
    d should haveChange(Shift("b", 1, 2))
  }

  ignore("should detect multiple shifts in a list") {
    val l1 = JList("a", "b", "c")
    val l2 = JList("c", "b", "a")

    val d = diff(l1, l2)
    d.leafChanges should have size 2
    d should haveChange(Shift("c", 2, 0))
    d should haveChange(Shift("a", 0, 2))
  }

  ignore("should detect multiple changes in a list") { // TODO detecting swap
    val l1 = JList("a", "b", "c")
    val l2 = JList("c", "x", "a")

    val d = diff(l1, l2)

    fail("todo")
  }

  test("should detect that just 1 element has been inserted") { // identity of value types = zero diff
    val o1 = JList(new NamedBean("a"), new NamedBean("c"))
    val b = new NamedBean("b")
    val o2 = JList(new NamedBean("a"), b, new NamedBean("c"))

    diff(o1, o2) should haveChange(new Insertion(b, 1))
    diff(o1, o2).leafChanges should have size 1
  }

  test("should detect difference of simple types") {
    diff(1, 2) should haveDifference
    diff("aa", "bb") should haveDifference
  }

  test("there should be no difference between sets when there are another differences in the parent") { // tests the optimization after lcs // TODO unit test for it
    val bean1 = new ParentBean("aaa", JSet(new NamedBean("a"), new NamedBean("b")))
    val bean2 = new ParentBean("bbb", JSet(new NamedBean("b"), new NamedBean("a")))

    diff(bean1, bean2) should not(haveDifference("child"))
  }

  test("should detect that there is no difference between beans with sets of beans") {
    val bean1 = new ParentBean("parent", JSet(new NamedBean("a"), new NamedBean("b")))
    val bean2 = new ParentBean("parent", JSet(new NamedBean("b"), new NamedBean("a")))

    diff(bean1, bean2) should not(haveDifference)
  }

  test("should calculate diff when 2 sets are on the path") { // tests handling of transformed targets when creating subdiffs // TODO ugly since ElementProperty introduction, refactor // TODO better test name
    val donald = new SimpleJavaBean("Donald", 1)
    val set1 = JSet(donald)
    val col1 = new CollectionBean(set1)
    val bean1 = new ParentBean("bean", JSet(col1))

    val col2 = new CollectionBean(JSet(new SimpleJavaBean("Sknerus", 1)))
    val bean2 = new ParentBean("bean", JSet(col2))

    val d = diff(bean1, bean2)

    d should haveDifference(Path(Property("child"), ElementProperty(col1), Property("collection"), ElementProperty(donald), Property("name"))) // TODO concise way to express it
  }

  test("should find difference between a sets within sets") {
    val nested1 = JSet(1)
    val set1 = JSet(nested1)
    val set2 = JSet(JSet(2))

    val d = diff(set1, set2)

    //d should haveDifference("[0][0]") // old way
    // d should haveDifference("[0]") // newer (but still old ;)) way
    d should haveDifference(ElementProperty(nested1))
    d should haveChange(ElementProperty(nested1), Removal(1))
    d should haveChange(ElementProperty(nested1), Addition(2))
  }

  test("subsequent calls should return the same result (difference)") {
    new SimpleBeans {
      val engine = BeanDiff.diffEngine().build()
      engine.calculateDiff(a1a, b1) should be === Diff(a1a, new NewValue(Property("name"), "a", "b"))
      engine.calculateDiff(a1a, b1) should be === Diff(a1a, new NewValue(Property("name"), "a", "b"))
    }
  }

  test("subsequent calls should return the same result (no difference)") {
    new SimpleBeans {
      val engine = BeanDiff.diffEngine().build()
      engine.calculateDiff(a1a, a1b) should not(haveDifference)
      engine.calculateDiff(a1a, a1b) should not(haveDifference)
    }
  }

  test("should not show difference when all leaf properties are the same") {
    new NestedBeans {
      val d = diff(parent1, parent1clone)
      d should not(haveDifference)
    }
  }

  test("should detect difference in lists of different size") { // TODO should deletion be attached to collection or particular index?
    new Collections {
      val d = diff(jList1, new ArrayList)
      val selfChanges = d.changes(EmptyPath).get.leafChanges

      selfChanges should have size 3
      selfChanges should haveDeletionAt(0)
      selfChanges should haveDeletionAt(1)
      selfChanges should haveDeletionAt(2)
    }
  }

  ignore("should detect difference in lists of different size (old)") { // TODO should deletion be attached to collection or particular index?
    new Collections {
      val d = diff(jList1, new ArrayList)

      d should haveDifference("[0]")
      d should haveDifference("[1]")
      d should haveDifference("[2]")
    }
  }

  ignore("should detect difference in list of lists (no-swap)") { // TODO tests as per initial impl (before LCS). Decide if it's now obsolete or not
    new Collections {
      val d = diff(Arrays.asList(jList1, jList2), Arrays.asList(jList2, jList3))

      d should haveDifference("[0][0]")
      d should haveDifference("[1][0]")
    }
  }

  ignore("should detect difference in list of lists (swap)") { // TODO tests as per initial impl (before LCS). Decide if it's now obsolete or not
    new Collections {
      val d = diff(Arrays.asList(jList1, jList2), Arrays.asList(jList2, jList1))

      d should haveDifference("[0][0]")
      d should haveDifference("[1][0]")
    }
  }

  test("should detect that hash sets have the same elements") {
    new Collections {
      diff(jSet1, jSet2) should not(haveDifference)
    }
  }

  test("should detect difference in sets") {
    new Collections {
      val d = diff(jSet1, jSet3)

      d should haveDifference
      d should haveChange(new Addition("ccc"))
      d should haveChange(new Removal("aaa"))
    }
  }

  test("should not hang on object cycle") {
    val p1 = new ParentBean("p1")
    val p2 = new ParentBean("p2", p1)
    p1.setChild(p2)

    val p3 = new ParentBean("p3")
    val p4 = new ParentBean("p4", p3)
    p3.setChild(p4)

    diff(p1, p3)
  }
  
  test("should correctly calculate diff of a object cycle") {
    val p1 = new ParentBean("p1")
    val p2 = new ParentBean("p2", p1)
    p1.setChild(p2)

    val p3 = new ParentBean("p3")
    val p4 = new ParentBean("p4", p3)
    p3.setChild(p4)

    val d = diff(p1, p3)
    d.leafChanges should have size 3
    d should haveDifference("name")
    d should haveDifference("child.name")
    d should haveDifference("child.child")
  }

  test("should detect differnces in java lists of strings") {
    new Collections {
      val d = diff(jList1, jList2)

      d should haveDifference("[0]")
    }
  }

  test("should detect exact difference in nested list of beans") {
    new CollectionBeans {
      val d = diff(beans1, beans2)

      d should not(haveDifference("collection[0]"))
      d should haveDifference("collection[1]")
      d should not(haveDifference("collection[1].name"))
      d should haveDifference("collection[1].value")
    }
  }

  test("should detect exact difference in nested list of strings") {
    new CollectionBeans {
      val d = diff(abc, abd)

      d should not(haveDifference("collection[0]"))
      d should not(haveDifference("collection[1]"))
      d should haveDifference("collection[2]")
    }
  }

  test("should detect difference in nested bean") {
    new NestedBeans {
      val d = diff(parent1, parent2)

      d should haveDifference
      d should haveDifference("child.value")
      d should haveDifference("child.name")
    }
  }

  test("should detect that all properties are equal") {
    new SimpleBeans {
      diff(a1a, a1b) should not(haveDifference)
    }
  }

  test("should detect that there is no difference") {
    new SimpleBeans {
      assert(a1a != a1b)
      val d = diff(a1a, a1a)
      d should not(haveDifference)
      d should not(haveDifference("name"))
    }
  }

  test("should detect difference in simple property") {
    new SimpleBeans {
      val d = diff(a1a, b1)
      d should haveDifference
      d should haveDifference("name")
    }
  }

  test("should handle null property on left") {
    new NestedBeans {
      parent1.setChild(null)
      diff(parent1, parent2) should haveDifference("child")
    }
  }

  test("should handle null property on right") {
    new NestedBeans {
      parent2.setChild(null)
      diff(parent1, parent2) should haveDifference("child")
    }
  }

  test("should show no difference if both properties are null") {
    new NestedBeans {
      parent1.setChild(null)
      parent2.setChild(null)
      diff(parent1, parent2) should not(haveDifference)
    }
  }

  test("should correctly present difference between sets") {
    new Collections {
      val writer = new StringWriter

      printDiff(new PrintWriter(writer), jSet1, jSet3)
      writer.toString() should startWith(". -- removed 'aaa'\n" + ". -- added 'ccc'")
    }
  }

  ignore("should correctly present difference between sets (old)") { // TODO the old way -- rethink & remove/refactor
    new Collections {
      val tmp = diff(jSet1, jSet3)

      val writer = new StringWriter
      printDiff(new PrintWriter(writer), jSet1, jSet3)
      writer.toString() should startWith("[0] -- 'aaa' vs 'bbb'\n[1] -- 'bbb' vs 'ccc'")
    }
  }

  test("if object appears twice but not at the same path, it's not a cycle") {
    val parent1 = new ParentBean("parent", new SimpleJavaBean("lucky", 8))
    val parent2 = new ParentBean("parent", new SimpleJavaBean("unlucky", 8))

    val d = diff(Arrays.asList(parent1, parent1), Arrays.asList(parent2, parent2))

    d should haveDifference("[0].child.name")
    d should haveDifference("[1].child.name")
  }

  test("an instertion to the list should be detected as a single difference") {
    val d = diff(Arrays.asList("default"), Arrays.asList("backgammon", "default"));
    d.changes should have size 1
  }

  test("should detect that an enity in a set was replaced with another one") {
    new Simpsons {
      val engine = diffEngine.withEntity[SimpleJavaBean]("value")

      val s1 = JSet(bart)
      val s2 = JSet(lisa)

      val d = engine.calculateDiff(s1, s2)
      d.leafChanges should have size 2
      d should haveChange(Removal(bart))
      d should haveChange(Addition(lisa))
    }
  }

  test("example from site should work as described") {
    val a = new Node("a")
    val b = new Node("b")
    val c = new Node("c")
    val x = new Node("x")

    val parent1 = new Node("parent1", a, b, c)
    val parent2 = new Node("parent2", a, x, c)

    val diff = BeanDiff.diff(parent1, parent2)

    diff should haveDifference
    diff should haveDifference("name")
    diff should not(haveDifference("children[0]"))
    diff should haveDifference("children[1].name")

    BeanDiff.mkString(diff) should be === "name -- 'parent1' vs 'parent2'\n" + "children[1].name -- 'b' vs 'x'\n"
  }

  test("should detect difference between fields in superclass") {
    val a = new DescendantJavaBean("a", 1, "x")
    val b = new DescendantJavaBean("b", 1, "x")

    diff(a, b) should haveDifference("name")
  }

  test("should be able to compare instance of derived class with an instance of base class") {
    val derived = new DescendantJavaBean("bbb", 2, "x")
    val base = new SimpleJavaBean("aaa", 1)

    val d = diff(derived, base)

    d should haveDifference("name")
    d should haveDifference("value")
    d should haveDifference("nickname")

    val expectedChange: (Path, Change) = (EmptyPath, NewValue(Property("nickname"), Some("x"), None))
    d.leafChanges should contain(expectedChange)
  }

  test("should be able to compare instance of base class with an instance of derived class") {
    val base = new SimpleJavaBean("aaa", 1)
    val derived = new DescendantJavaBean("bbb", 2, "x")

    val d = diff(base, derived)

    d should haveDifference("name")
    d should haveDifference("value")
    d should haveDifference("nickname")

    val expectedChange: (Path, Change) = (EmptyPath, NewValue(Property("nickname"), None, Some("x")))
    d.leafChanges should contain(expectedChange)
  }

  test("should compare instances of 2 different classes with the same fields") {
    val o1 = new SimpleJavaBean("a", 7)
    val o2 = new Object() {
      private val name = "b"
      private val value = 9
    }

    val d = diff(o1, o2)
    d.leafChanges should have size 2
    d should haveDifference("name")
    d should haveDifference("value")
  }

  test("should see that instances of 2 different have no difference in fields") {
    val o1 = new SimpleJavaBean("a", 7)
    val o2 = new Object() {
      private val name = "a"
      private val value = 7
    }

    diff(o1, o2) should not(haveDifference)
  }

  test("should detect that null in list has been changed to int") {
    val l1 = JList(null)
    val l2 = JList(1)
    
    diff(l1, l2) should haveDifference("[0]")
  }
  
  test("should detect that entry in a map has changed") {
    val m1 = JMap("a" -> 1, "b" -> 2)
    val m2 = JMap("a" -> 9, "b" -> 2)

    val d = diff(m1, m2)

    d.leafChanges should be === List((EmptyPath, NewValue(Property("[a]"), 1, 9)))
  }

  test("should detect that a value has been associated with a new key") {
    val m1 = JMap("a" -> 1, "b" -> 2)
    val m2 = JMap("a" -> 1, "b" -> 2, "c" -> 3)

    diff(m1, m2).leafChanges should be === List((EmptyPath, Association("c", 3)))
  }

  test("should detect that a map entry has been removed") {
    val m1 = JMap("a" -> 1, "b" -> 2, "c" -> 3)
    val m2 = JMap("a" -> 1, "b" -> 2)

    diff(m1, m2).leafChanges should be === List((EmptyPath, KeyRemoval("c", 3)))
  }
  
  test("should handle change of a null value in map") {
    val m1 = JMap("a" -> null)
    val m2 = JMap("a" -> 1)
    
    diff(m1, m2).leafChanges should be === List((EmptyPath, NewValue(Property("[a]"), null, 1)))
  }
  
  test("should detect that a map value has changed to null") {
    val m1 = JMap("a" -> 1)
    val m2 = JMap("a" -> null)
    
    diff(m1, m2).leafChanges should be === List((EmptyPath, NewValue(Property("[a]"), 1, null)))
  }
  
  test("should detect that property of a value in the map has changed") {
    val m1 = JMap("a" -> new SimpleJavaBean("a", 1))
    val m2 = JMap("a" -> new SimpleJavaBean("a", 2))
    
    diff(m1, m2) should haveDifference("[a].value")
  }
  
  test("should detect that entity in a map has been replaced with another entity") {
    val a1 = new SimpleJavaBean("a", 1)
    val a2 = new SimpleJavaBean("a", 2)
    
    val engine = diffEngine().withEntity[SimpleJavaBean]("value")
    
    val m1 = JMap("a" -> a1)
    val m2 = JMap("a" -> a2)
    
    val diff = engine.calculateDiff(m1, m2)
    diff should haveChange(NewValue(Property("[a]"), a1, a2))
  }
  
  test("should detect no difference between maps with simple keys & values") {
    val m1 = JMap("a" -> "A", "b" -> "B")
    val m2 = JMap("a" -> "A", "b" -> "B")
    
    diff(m1, m2) should not (haveDifference)
  }
  
  test("should detect no difference between between maps") { // SimpleJavaBean has not overridden equals/hashcode
    val m1 = JMap("a" -> new SimpleJavaBean("a", 1))
    val m2 = JMap("a" -> new SimpleJavaBean("a", 1))
    
    diff(m1, m2) should not (haveDifference)
  }
  
  test("should detect that property of an entity in a map has changed") {
    val a1 = new SimpleJavaBean("a", 1)
    val a2 = new SimpleJavaBean("a", 2)
    
    val engine = diffEngine().withEntity[SimpleJavaBean]("name")
    
    val m1 = JMap("a" -> a1)
    val m2 = JMap("a" -> a2)
    
    val diff = engine.calculateDiff(m1, m2)
    diff should haveChange(Path("[a]"), NewValue(Property("value"), 1, 2))
  }
  
  test("should just use equals when comparing collection with non-collection") {
    new Simpsons {
      diff(JList(1, 2), bart).leafChanges should be === List((EmptyPath, NewValue(Self, JList(1, 2), bart)))
      diff(bart, JList(1, 2)).leafChanges should be === List((EmptyPath, NewValue(Self, bart, JList(1, 2))))
    }
  }
  
  test("should detect difference in values associated with a null-key") {
    val m1 = JMap((null, "a"))
    val m2 = JMap((null, "b"))
    
    diff(m1, m2).leafChanges should be === List((EmptyPath, NewValue(KeyProperty(null), "a", "b")))
  }
  
  ignore("should not merge Deletion+Removal into subdiff when classes are different (unless configured otherwise)") {
    val o1 = new Object {
      val name = "o1"
    }
    val o2 = new Object {
      val name = "o2"
    }
    
    diff(JList(o1), JList(o2)) should be === Diff(JList(o1), Deletion(o1, 0), Insertion(o2, 0))
  }
}