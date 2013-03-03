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
import org.beandiff.BeanDiff.IgnoreCase
import org.beandiff.BeanDiff.diff
import org.beandiff.BeanDiff.printDiff
import org.beandiff.BeanDiff.aDiffEngine
import org.beandiff.beans.CollectionBean
import org.beandiff.beans.NamedBean
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.Diff
import org.beandiff.core.model.Property
import org.beandiff.core.model.Self
import org.beandiff.core.model.Path.EmptyPath
import org.beandiff.core.model.change.Addition
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.NewValue
import org.beandiff.core.model.change.Removal
import org.beandiff.test.BeanDiffMatchers.haveChange
import org.beandiff.test.BeanDiffMatchers.haveDeletionAt
import org.beandiff.test.BeanDiffMatchers.haveDifference
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.Node
import org.beandiff.core.model.change.Shift
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.core.model.change.Deletion
import org.beandiff.core.model.change.Insertion
import org.beandiff.beans.Simpsons
import org.beandiff.core.model.change.Addition


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
  
  // TODO
  ignore("should compare elements of different type") {
    val o1 = new SimpleJavaBean("name", 1)
    val o2 = new ParentBean("name", 2)
    
    val d = diff(o1, o2)
    d should haveDifference("@type")
    d should haveDifference("child") // ParentBean's property
    d should haveDifference("value") // SimpleJavaBean's property
  }
  
  // TODO
  ignore("should handle elements of different types in a list") {
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
    
    diff(bean1, bean2) should not (haveDifference("child"))
  }
  
  test("should detect that there is no difference between beans with sets of beans") {
    val bean1 = new ParentBean("parent", JSet(new NamedBean("a"), new NamedBean("b")))
    val bean2 = new ParentBean("parent", JSet(new NamedBean("b"), new NamedBean("a")))
    
    diff(bean1, bean2) should not (haveDifference)
  }
  
  test("should calculate diff when 2 sets are on the path") { // tests handling of transformed targets when creating subdiffs
    val col1 = new CollectionBean(JSet(new SimpleJavaBean("Donald", 1)))
    val bean1 = new ParentBean("bean", JSet(col1))

    val col2 = new CollectionBean(JSet(new SimpleJavaBean("Sknerus", 1)))
    val bean2 = new ParentBean("bean", JSet(col2))
    
    val d = diff(bean1, bean2)

    d should haveDifference("child[0].collection[0].name")
  }
  
  test("should find difference between a sets within sets") {
    val set1 = JSet(JSet(1))
    val set2 = JSet(JSet(2))
    
    val d = diff(set1, set2)
    
    //d should haveDifference("[0][0]") // old way
    d should haveDifference("[0]")
    d should haveChange("[0]", Removal(1))
    d should haveChange("[0]", Addition(2))
  }
  
  test("subsequent calls should return the same result (difference)") {
    new SimpleBeans {
      val engine = BeanDiff.diffEngine()
      engine.calculateDiff(a1a, b1) should be === Diff(a1a, new NewValue(Property("name"), "a", "b"))
      engine.calculateDiff(a1a, b1) should be === Diff(a1a, new NewValue(Property("name"), "a", "b"))
    }
  }
  
  test("subsequent calls should return the same result (no difference)") {
    new SimpleBeans {
      val engine = BeanDiff.diffEngine()
      engine.calculateDiff(a1a, a1b) should not (haveDifference)
      engine.calculateDiff(a1a, a1b) should not (haveDifference)
    }
  }
  
  test("should not show difference when all leaf properties are the same") {
    new NestedBeans {
      val d = diff(parent1, parent1clone)
      d should not (haveDifference)
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
      diff(jSet1, jSet2) should not (haveDifference)
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
  
  test("should be case sensitive if not specified otherwise") {
    new SimpleBeans {
      diff(a1a, A1) should haveDifference
    }
  }
  
  test("should ignore case if requested") {
    new SimpleBeans {
      diff(a1a, A1, IgnoreCase) should not (haveDifference)
    }
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
      
      d should not (haveDifference("collection[0]"))
      d should haveDifference("collection[1]")
      d should not (haveDifference("collection[1].name"))
      d should haveDifference("collection[1].value")
    }
  }
  
  test("should detect exact difference in nested list of strings") {
    new CollectionBeans {
      val d = diff(abc, abd)
      
      d should not (haveDifference("collection[0]"))
      d should not (haveDifference("collection[1]"))
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
      diff(a1a, a1b) should not (haveDifference)
    }
  }
  
  test("should detect that there is no difference") {
    new SimpleBeans {
      assert(a1a != a1b)
      val d = diff(a1a, a1a)
      d should not (haveDifference)
      d should not (haveDifference("name"))
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
      diff(parent1, parent2) should not (haveDifference)
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
      val engine = aDiffEngine.withEntity[SimpleJavaBean]("value")

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
    diff should not (haveDifference("children[0]"))
    diff should haveDifference("children[1].name")
    
    BeanDiff.mkString(diff) should be === "name -- 'parent1' vs 'parent2'\n" + "children[1].name -- 'b' vs 'x'\n"
  }
}