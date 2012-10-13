package org.beandiff

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import org.beandiff.core.Diff
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.core.Path
import org.beandiff.core.Property
import org.beandiff.core.ObjectWalker
import scala.collection.mutable.ListBuffer
import org.beandiff.core.LeafDiff
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.IgnoreCaseStringEqualityInvestigator
import org.beandiff.core.DiffEngine
import org.beandiff.support.ClassDictionary
import org.beandiff.equality.StdEqualityInvestigator

/**
 * A container for syntactic sugar methods
 *
 * @author Tomasz Kaczmarzyk
 */
object BeanDiff {

  val descStrategy = EndOnSimpleTypeStrategy

  def diff(o1: Any, o2: Any): Diff = {
    diff(o1, o2, null)
  }

  def diff(o1: Any, o2: Any, modifiers: Any*): Diff = {
    val eqInvestigators = new ClassDictionary[EqualityInvestigator](
      new StdEqualityInvestigator, getEqInvestigatorMappings(modifiers.toList))

    new DiffEngine(eqInvestigators).calculateDiff(o1, o2)
  }

  val ignoreCase =
    (classOf[String], new IgnoreCaseStringEqualityInvestigator)

  private def getEqInvestigatorMappings(objects: List[_]) = {
    objects.filter(_.isInstanceOf[(Class[_], EqualityInvestigator)])
      .asInstanceOf[Iterable[(Class[_], EqualityInvestigator)]]
  }
}