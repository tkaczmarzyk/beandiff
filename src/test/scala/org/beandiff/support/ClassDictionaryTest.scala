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
package org.beandiff.support

import org.beandiff.TestDefs.mock
import org.beandiff.TypeDefs._
import org.beandiff.test.JList
import org.beandiff.test.JSet
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.util.ArrayList
import java.util.AbstractList
import java.util.LinkedList
import java.util.TreeSet
import java.util.HashSet
import java.util.AbstractSequentialList
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ClassDictionaryTest extends FunSuite with ShouldMatchers {

  val defaultVal = mock[Object]("defaultVal")
  val valForSets = mock[Object]("valForSets")
  val valForSortedSets = mock[Object]("valForSortedSets")
  val valForLists = mock[Object]("valForLists")
  val valForAbstractSeqLists = mock[Object]("valForAbstractSeqLists")
  val valForArrayList = mock[Object]("valForArrayList")
  val valForString = mock[Object]("valForString")
  
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
    val mockHashSet = mock[HashSet[_]]
    assert(dict(mockHashSet.getClass()) === valForSets)    
  }
  
  test("should find value for interface") {
    val mockList = mock[JList]
    assert(dict(mockList.getClass) === valForLists)
  }
  
  test("should return value for the first candidate's class") {
    dict(JList(), JSet()) should be === valForArrayList
  }
  
  test("should return value for the second candidate's class") {
    dict(null, JSet()) should be === valForSets
  }
  
  test("should return default value when both candidates null") {
    dict(null, null) should be === dict.defaultValue
  }
}