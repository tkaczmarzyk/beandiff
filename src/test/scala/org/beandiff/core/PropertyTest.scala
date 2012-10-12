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
  
}