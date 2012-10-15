package org.beandiff

import org.beandiff.core.Diff
import org.beandiff.core.DiffEngine
import org.beandiff.core.EndOnSimpleTypeStrategy
import org.beandiff.equality.EqualityInvestigator
import org.beandiff.equality.IgnoreCaseStringEqualityInvestigator
import org.beandiff.equality.StdEqualityInvestigator
import org.beandiff.support.ClassDictionary
import java.io.PrintStream
import org.beandiff.display.PlainTextDiffPresenter
import org.beandiff.core.BreakCycleStrategy

/**
 * A container for syntactic sugar methods
 *
 * @author Tomasz Kaczmarzyk
 */
object BeanDiff {

  val descStrategy = EndOnSimpleTypeStrategy
  
  val ignoreCase =
    (classOf[String], new IgnoreCaseStringEqualityInvestigator)

    
  def diff(o1: Any, o2: Any): Diff = {
    diff(o1, o2, List() : _*)
  }

  def diff(o1: Any, o2: Any, modifiers: Any*): Diff = {
    val eqInvestigators = new ClassDictionary[EqualityInvestigator](
      new StdEqualityInvestigator, getEqInvestigatorMappings(modifiers.toList))

    new DiffEngine(eqInvestigators, new BreakCycleStrategy(EndOnSimpleTypeStrategy)).calculateDiff(o1, o2)
  }
  
  def printDiff(o1: Any, o2: Any): Unit = 
    printDiff(o1, o2, List() : _*)
  
  def printDiff(o1: Any, o2: Any, modifiers: Any*): Unit =
    printDiff(System.out, o1, o2, modifiers : _*)
  
  def printDiff(out: PrintStream, o1: Any, o2: Any): Unit =
    printDiff(out, o1, o2, List() : _*)
    
  def printDiff(out: PrintStream, o1: Any, o2: Any, modifiers: Any*) = {
    val presenter = new PlainTextDiffPresenter
    out.println(presenter.present(diff(o1, o2, modifiers : _*)))
  }

  private def getEqInvestigatorMappings(objects: List[_]) = {
    objects.filter(_.isInstanceOf[(Class[_], EqualityInvestigator)])
      .asInstanceOf[Iterable[(Class[_], EqualityInvestigator)]]
  }
}