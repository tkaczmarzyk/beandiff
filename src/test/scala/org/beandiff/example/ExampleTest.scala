package org.beandiff.example

import org.beandiff.beans.SimpleJavaBean
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExampleTest extends FunSuite {

  val bean = new SimpleJavaBean("bean1", 1)

  test("dummy test: should equal to itself") {
    assert(bean === bean)
  }
}