package org.beandiff.core

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator

class DiffEngine(
    private val eqInvestigators: ClassDictionary[EqualityInvestigator],
    private val descStrategy: DescendingStrategy) {

  
  def calculateDiff(o1: Any, o2: Any) = {
    val d = new Diff(o1, o2)
    
    new ObjectWalker(new BreakCycleStrategy(descStrategy),
      (path, val1, val2, isLeaf) =>
        if (!eqInvestigators(val1.getClass).areEqual(val1, val2)) {
          d(path) = if (isLeaf) new LeafDiff(val1, val2) else new Diff(val1, val2)
        }
    ).walk(o1, o2)
    
    d
  }
}