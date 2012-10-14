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
  }
  
  
  ignore("should detect differnces in scala lists of strings") {
    new Collections {
      val d = diff(sList1, sList2)
      
      assert(d.hasDifference("[0]"))
    }
  }
  
  test("should not hang on object cycle") {
    val p1 = new ParentBean("p1")
    val p2 = new ParentBean("p2", p1)
    p1.setChild(p2)
    
    diff(p1, p2)
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
}