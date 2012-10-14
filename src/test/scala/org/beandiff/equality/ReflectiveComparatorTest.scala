package org.beandiff.equality

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.SimpleJavaBean
import java.util.ArrayList
import java.util.Arrays
import org.beandiff.beans.ParentBean

@RunWith(classOf[JUnitRunner])
class ReflectiveComparatorTest extends FunSuite with ShouldMatchers {

  val comparator = new ReflectiveComparator

  trait SimpleBeans {
    val a1 = new SimpleJavaBean("a", 1)
    val a1a = new SimpleJavaBean("a", 1)
    val a2 = new SimpleJavaBean("a", 2)
    
    val b1 = new SimpleJavaBean("b", 1)
    val b2 = new SimpleJavaBean("b", 2)
  }
  
  trait ParentBeans extends SimpleBeans {
    val a1parent = new ParentBean(a1)
    val a1parent2 = new ParentBean(a1)
    val a2parent = new ParentBean(a2)
  }
  
  trait Collections extends SimpleBeans {
    val a1a1List = Arrays.asList(a1, a1)
    val a1a2List = Arrays.asList(a1, a2)
    val a1a2List2 = Arrays.asList(a1, a2)
  }
  
  test("should compare 2 strings") {
    comparator.compare("aaa", "aaa") should be === (0)
    comparator.compare("aaa", "abb") should be < (0)
    comparator.compare("abb", "aaa") should be > (0)
  }
  
  test("null should be after non-null") {
    comparator.compare(null, "aaa") should be > 0
    comparator.compare("aaa", null) should be < 0
  }
  
  test("should detect equal objects") {
    new SimpleBeans {
      comparator.compare(a1, a1a) should be === 0
    }
  }
  
  test("should always check fields in the same order") {
    new SimpleBeans {
      if (comparator.compare(a2, b1) > 0)
        comparator.compare(b1, a2) should be < 0
      else
        comparator.compare(b1, a2) should be > 0
    }
  }
  
  test("should compare comparable properties") {
    new SimpleBeans {
      comparator.compare(a1, a2) should be < 0
      comparator.compare(a2, a1) should be > 0
    }
  }
  
  test("should compare 2 lists") {
    new Collections {
      comparator.compare(a1a2List, a1a2List2) should be === 0
      comparator.compare(a1a1List, a1a2List) should be < 0
      comparator.compare(a1a2List, a1a1List) should be > 0
    }
  }
  
  test("should compare not comparable properties") {
    new ParentBeans {
      comparator.compare(a1parent, a1parent2) should be === 0
      comparator.compare(a1parent, a2parent) should be < 0
      comparator.compare(a2parent, a1parent) should be > 0
    }
  }
}