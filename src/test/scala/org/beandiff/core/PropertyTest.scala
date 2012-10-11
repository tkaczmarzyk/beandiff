package org.beandiff.core

import org.scalatest.FunSuite
import org.beandiff.beans.SimpleJavaBean
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.beandiff.beans.ParentBean

@RunWith(classOf[JUnitRunner])
class PropertyTest extends FunSuite {

  trait TestBeans {
    val javaBean1 = new SimpleJavaBean("javaBean1", 1)
    val parentBean1 = new ParentBean(javaBean1)
  }
  
  test("should return value of a single private field") {
    new TestBeans {
       val prop = new Property(classOf[SimpleJavaBean], "name")
       assert(prop.value(javaBean1) === "javaBean1")
    }
  }
  
  ignore("should return value of a nested property") {
    new TestBeans {
      val prop = new Property(classOf[ParentBean], "child.name")
      assert(prop.value(parentBean1) === "javaBean1")
    }
  }
}