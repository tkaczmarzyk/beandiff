package org.beandiff.core

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PathTest extends FunSuite {

  test("should parse index property followed by field property") {
    assert(Path.of("[1].name") === new Path(new IndexProperty(1), new Path(new FieldProperty("name"))))
  }
  
  test("should parse single index property") {
    assert(Path.of("[19]") === new Path(new IndexProperty(19)))
  }
  
  test("should parse a sequence of indexes") {
    assert(Path.of("a[1][0]") === new Path(new FieldProperty("a"), new Path(new IndexProperty(1), new Path(new IndexProperty(0)))))
  }
  
  test("should parse index property") {
    assert(Path.of("a[1]") === new Path(new FieldProperty("a"), new Path(new IndexProperty(1))))
  }
  
  test("should parse chain of property names") {
    assert(Path.of("a.b") === new Path(new FieldProperty("a"), new Path(new FieldProperty("b"))))
  }
}