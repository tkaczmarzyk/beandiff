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
import org.beandiff.core.DiffEngine
import org.beandiff.test.JMap

trait TransformationCheckUtils extends Checkers {

  def transformsJCollections[T](a: List[T], b: List[T])(implicit engine: DiffEngine): Boolean = {
    transformsJLists(a, b) && transformsJSets(a, b)
  }
  
  def transformsJLists[T](a: List[T], b: List[T])(implicit engine: DiffEngine): Boolean = {
    val l1 = JList(a: _*)
    val l2 = JList(b: _*)

    engine.calculateDiff(l1, l2).transformTarget()
    l1 == l2
  }
  
  def transformsJSets[T](a: List[T], b: List[T])(implicit engine: DiffEngine): Boolean = {
    val s1 = JSet(a: _*)
    val s2 = JSet(b: _*)
    
    engine.calculateDiff(s1, s2).transformTarget()
    s1 == s2
  }
  
  def transformsJMaps[K, V](a: Map[K, V], b: Map[K, V])(implicit engine: DiffEngine): Boolean = {
    val m1 = JMap(a.toList: _*)
    val m2 = JMap(b.toList: _*)
    
    engine.calculateDiff(m1, m2).transformTarget()
    m1 == m2
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

  val genMap = for {
    numEntries <- Gen.choose(0, 20)
    keys = Gen.choose(-10, 10).take(numEntries)
    parents = genParent.take(numEntries)
    entries = keys.zip(parents)
  } yield Map(entries: _*)
  
  implicit val arbitraryParent = Arbitrary(Gen.resize(5, Gen.containerOf[List, Parent](genParent)))
  
  implicit val arbitraryMap = Arbitrary(genMap)
  
  implicit class RichGen[T](gen: Gen[T]) {
    def take(n: Int): Seq[T] = {
      val elems = for (i <- 0 until n)
        yield gen.apply(new Gen.Params).getOrElse(null)
      elems.asInstanceOf[Seq[T]] // FIXME
    }
  }
}