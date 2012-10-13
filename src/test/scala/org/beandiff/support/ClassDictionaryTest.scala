package org.beandiff.support

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.util.ArrayList
import java.util.AbstractList
import java.util.LinkedList
import org.mockito.Mockito.mock
import java.util.TreeSet
import java.util.HashSet
import java.util.AbstractSequentialList

@RunWith(classOf[JUnitRunner])
class ClassDictionaryTest extends FunSuite {

  val defaultVal = mock(classOf[Object], "defaultVal")
  val valForSets = mock(classOf[Object], "valForSets")
  val valForSortedSets = mock(classOf[Object], "valForSortedSets")
  val valForLists = mock(classOf[Object], "valForLists")
  val valForAbstractSeqLists = mock(classOf[Object], "valForAbstractSeqLists")
  val valForArrayList = mock(classOf[Object], "valForArrayList")
  val valForString = mock(classOf[Object], "valForString")
  
  val dict = new ClassDictionary(defaultVal,
      (classOf[java.util.List[_]] -> valForLists),
      (classOf[AbstractSequentialList[_]] -> valForAbstractSeqLists),
      (classOf[ArrayList[_]] -> valForArrayList),
      (classOf[java.util.Set[_]] -> valForSets),
      (classOf[java.util.SortedSet[_]] -> valForSortedSets),
      (classOf[String] -> valForString))
  
  test("should find value associated directly") {
    assert(dict(classOf[String]) === valForString)
  }
  
  test("should choose value associated directly") {
    assert(dict(classOf[ArrayList[_]]) === valForArrayList)
  }
  
  test("should return default value if key not found") {
    assert(dict(classOf[Integer]) === defaultVal)
  }
  
  test("should prefer value for superclass than for interface") {
    assert(dict(classOf[LinkedList[_]]) === valForAbstractSeqLists)
  }
  
  test("should prefer more specific interface") {
    assert(dict(classOf[TreeSet[_]]) === valForSortedSets)
  }
  
  test("should find value for superclass'es interface") {
    val mockHashSet = mock(classOf[HashSet[_]])
    assert(dict(mockHashSet.getClass()) === valForSets)    
  }
  
  test("should find value for interface") {
    val mockList = mock(classOf[java.util.List[_]])
    assert(dict(mockList.getClass) === valForLists)
  }
  
}