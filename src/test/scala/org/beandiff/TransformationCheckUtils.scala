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
package org.beandiff

import org.beandiff.beans.scala.Child
import org.beandiff.beans.scala.Parent
import org.beandiff.test.JList
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.Checkers
import org.beandiff.test.JSet

trait TransformationCheckUtils extends Checkers {

  def transformsJCollections[T](a: List[T], b: List[T]): Boolean = {
    transformsJLists(a, b) && transformsJSets(a, b)
  }
  
  private def transformsJLists[T](a: List[T], b: List[T]): Boolean = {
    val l1 = JList(a: _*)
    val l2 = JList(b: _*)

    BeanDiff.diff(l1, l2).transformTarget()
    l1 == l2
  }
  
  private def transformsJSets[T](a: List[T], b: List[T]): Boolean = {
    val s1 = JSet(a: _*)
    val s2 = JSet(b: _*)
    
    BeanDiff.diff(s1, s2).transformTarget()
    s1 == s2
  }

  val genName = Gen.oneOf("a", "b", "c", "d")
//    Gen.alphaStr.map { (str: String) =>
//    if (str.length() > 5) str.substring(0, 5)
//    else str
//  }

  val genChild = for {
    name <- genName
    age <- Gen.choose(-5, 5)
  } yield Child(name, age)

  val genParent = for {
    name <- genName
    n <- Gen.choose(0, 10)
    children = (1 to n).map((i: Int) =>
      genChild.sample match {
        case Some(child) => child
        case None => null
      })
  } yield Parent(name, JList(children: _*))

  implicit val arbitraryParent = Arbitrary(Gen.resize(5, Gen.containerOf[List, Parent](genParent)))
}