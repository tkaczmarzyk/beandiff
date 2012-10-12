package org.beandiff

import org.beandiff.BeanDiff.diff
import org.beandiff.beans.ParentBean
import org.beandiff.beans.SimpleJavaBean
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.CollectionBean
import java.util.ArrayList

@RunWith(classOf[JUnitRunner])
class BeanDiffTest extends FunSuite with ShouldMatchers {

  trait SimpleBeans {
    val a1a = new SimpleJavaBean("a", 1)
    val a1b = new SimpleJavaBean("a", 1)
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
  
  
  test("should detect exact difference in collection of beans") {
    new CollectionBeans {
      val d = diff(beans1, beans2)
      assert(!d.hasDifference("collection[0]"))
      assert(d.hasDifference("collection[1]"))
      assert(!d.hasDifference("collection[1].name"))
      assert(d.hasDifference("collection[1].value"))
    }
  }
  
  test("should detect exact difference in collection of strings") {
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