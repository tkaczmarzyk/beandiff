package org.beandiff.core

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator

class DiffEngine(
  private val eqInvestigators: ClassDictionary[EqualityInvestigator],
  private val descStrategy: DescendingStrategy) {

  def calculateDiff(o1: Any, o2: Any) = {
    val d = new Diff(o1, o2)

    new ObjectWalker(new EndOnNullStrategy(descStrategy),
      (path, val1, val2, isLeaf) =>
        if (!getEqInvestigator(val1, val2).areEqual(val1, val2)) {
          d(path) = if (isLeaf) new LeafDiff(val1, val2) else new Diff(val1, val2)
        }
    ).walk(o1, o2)

    d
  }

  private def getEqInvestigator(val1: Any, val2: Any) = {
    if (val1 == null && val2 == null)
      eqInvestigators.defaultValue
    else {
      val nonNull = if (val1 != null) val1 else val2
      eqInvestigators(nonNull.getClass)
    }
  }
}