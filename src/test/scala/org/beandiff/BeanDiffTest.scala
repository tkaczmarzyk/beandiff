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

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import org.beandiff.test.BeanDiffMatchers._
import org.beandiff.BeanDiff.diff
import org.beandiff.BeanDiff.printDiff
import org.beandiff.BeanDiff.ignoreCase
import org.beandiff.beans.CollectionBean
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.beandiff.core.model.FieldProperty
import org.beandiff.core.model.Self
import org.beandiff.core.model.Path
import org.beandiff.core.model.IndexProperty
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import java.io.StringWriter
import org.beandiff.test.TestConversions._
import java.io.PrintWriter
import org.beandiff.test.JSet
import org.beandiff.test.JList


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
  
  
  ignore("should detect differnces in scala lists of strings") {
    new Collections {
      val d = diff(sList1, sList2)
      
      assert(d.hasDifference("[0]"))
    }
  }
  
  // TODO:
  ignore("should calculate diff when 2 sets are on the path") { // tests handling of transformed targets when creating subdiffs
    val col1 = new CollectionBean(JSet(new SimpleJavaBean("Donald", 1)))
    val bean1 = new ParentBean("bean", JSet(col1))

    val col2 = new CollectionBean(JSet(new SimpleJavaBean("Sknerus", 1)))
    val bean2 = new ParentBean("bean", JSet(col2))
    
    val d = diff(bean1, bean2)

    d should haveDifference("child[0].collection[0].name")
  }
  
  test("should find difference in a sets within sets") {
    val set1 = JSet(JSet(1))
    val set2 = JSet(JSet(2))
    
    val d = diff(set1, set2)
    
    d should haveDifference("[0][0]")
  }
  
  test("subsequent calls should return the same result") { // TODO similar test with a difference expected
    new SimpleBeans {
      assert(!diff(a1a, a1b).hasDifference)
      assert(!diff(a1a, a1b).hasDifference)
    }
  }
  
  test("should detect difference of simple types") {
    assert(diff(1, 2).hasDifference)
    assert(diff("aa", "bb").hasDifference)
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
      val selfChanges = d.changes.filter(_._1 == Self).head._2.leafChanges // FIXME simplify
      
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
      assert(d.hasDifference("[1]"))
      assert(d.hasDifference("[2]"))
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
      assert(!diff(jSet1, jSet2).hasDifference)
    }
  }
  
  test("should detect difference in sets") {
    new Collections {
      assert(diff(jSet1, jSet3).hasDifference)
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
      assert(diff(a1a, A1).hasDifference)
    }
  }
  
  test("should ignore case if requested") {
    new SimpleBeans {
      assert(!diff(a1a, A1, ignoreCase).hasDifference)
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
      
      assert(d.hasDifference)
      assert(d.hasDifference("child.value"))
      assert(d.hasDifference("child.name"))
    }
  }
  
  test("should detect that all properties are equal") {
    new SimpleBeans {
      assert(!diff(a1a, a1b).hasDifference)
    }
  }
  
  test("should detect that there is no difference") {
    new SimpleBeans {
      assert(a1a != a1b)
      val d = diff(a1a, a1a)
      assert(!d.hasDifference)
      assert(!d.hasDifference("name"))
    }
  }
  
  test("should detect difference in simple property") {
    new SimpleBeans {
      val d = diff(a1a, b1)
      assert(d.hasDifference)
      assert(d.hasDifference("name"))
    }
  }
  
  test("should handle null property on left") {
    new NestedBeans {
      parent1.setChild(null)
      assert(diff(parent1, parent2).hasDifference("child"))
    }
  }
  
  test("should handle null property on right") {
    new NestedBeans {
      parent2.setChild(null)
      assert(diff(parent1, parent2).hasDifference("child"))
    }
  }
  
  test("should show no difference if both properties are null") {
    new NestedBeans {
      parent1.setChild(null)
      parent2.setChild(null)
      assert(!diff(parent1, parent2).hasDifference)
    }
  }
  
  test("should correctly present difference between sets") {
    new Collections {
      val tmp = diff(jSet1, jSet3)
      
      val writer = new StringWriter
      printDiff(new PrintWriter(writer), jSet1, jSet3)
      writer.toString() should startWith(" -- removed 'aaa'\n -- added 'ccc'") // TODO [1] vs [2] --> insertion index after or before the deletion?
    }
  }
  
  ignore("should correctly present difference between sets (old)") { // FIXME the old way -- rethink & remove/refactor
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
}