package org.beandiff.core

import org.beandiff.support.ClassDictionary
import org.beandiff.equality.EqualityInvestigator

class DiffEngine(
    private val eqInvestigators: ClassDictionary[EqualityInvestigator],
    private val descStrategy: DescendingStrategy) {

  
  def calculateDiff(o1: Any, o2: Any) = {
    val d = new Diff()
    
    new ObjectWalker(descStrategy,
      (path, val1, val2, isLeaf) =>
        if (!eqInvestigators(val1.getClass).areEqual(val1, val2)) {
          d(path) = if (isLeaf) new LeafDiff else new Diff()
        }
    ).walk(o1, o2)
    
    d
  }
}