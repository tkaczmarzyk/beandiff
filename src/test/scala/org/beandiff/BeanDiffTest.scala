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
package org.beandiff

import org.beandiff.BeanDiff.diff
import org.beandiff.BeanDiff.ignoreCase
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.CollectionBean
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import com.google.common.collect.Sets
import java.util.IdentityHashMap
import org.beandiff.core.Property
import org.beandiff.core.IndexProperty
import core.FieldProperty

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
  
  test("subsequent calls should return the same result") {
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
      assert(!d.hasDifference)
    }
  }
  
  test("should add compared objects to all Diff instances") { // TODO concise version once more methods are implemented in Diff (like getLeft, getRight(path))
    new CollectionBeans {
      val d = diff(beans1, beans2)
      
      d.o1 should not be === (null)
      d.o2 should not be equal (null)
      
      val collectionDiff = d.diffs(new FieldProperty("collection"))
      collectionDiff.o1 should not be === (null)
      collectionDiff.o2 should not be === (null)
      
      val indexDiff1 = collectionDiff.diffs(new IndexProperty(0))
      indexDiff1.o1 should not be === (null)
      indexDiff1.o2 should not be === (null)
      
      val indexDiff2 = collectionDiff.diffs(new IndexProperty(1))
      indexDiff1.o1 should not be === (null)
      indexDiff2.o2 should not be === (null)
    }
  }
  
  test("should detect difference in lists of different size") {
    new Collections {
      val d = diff(jList1, new ArrayList)
      assert(d.hasDifference("[0]"))
      assert(d.hasDifference("[1]"))
      assert(d.hasDifference("[2]"))
    }
  }
  
  test("should detect difference in list of lists") {
    new Collections {
      val d = diff(Arrays.asList(jList1, jList2), Arrays.asList(jList2, jList1))
      assert(d.hasDifference)
      assert(d.hasDifference("[0][0]"))
      assert(d.hasDifference("[1][0]"))
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
      
      assert(d.hasDifference("[0]"))
    }
  }
  
  test("should detect exact difference in nested list of beans") {
    new CollectionBeans {
      val d = diff(beans1, beans2)
      assert(!d.hasDifference("collection[0]"))
      assert(d.hasDifference("collection[1]"))
      assert(!d.hasDifference("collection[1].name"))
      assert(d.hasDifference("collection[1].value"))
    }
  }
  
  test("should detect exact difference in nested list of strings") {
    new CollectionBeans {
      val d = diff(abc, abd)
      assert(d.hasDifference)
      assert(!d.hasDifference("collection[0]"))
      assert(!d.hasDifference("collection[1]"))
      assert(d.hasDifference("collection[2]"))
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
  
  test("should show now difference if both properties are null") {
    new NestedBeans {
      parent1.setChild(null)
      parent2.setChild(null)
      assert(!diff(parent1, parent2).hasDifference)
    }
  }
}