package org.beandiff

import org.beandiff.core.Diff
import org.beandiff.core.FieldTraverser
import org.beandiff.core.EndOnSimpleTypeStrategy

object BeanDiff {

  def descStrategy = EndOnSimpleTypeStrategy
  
  
  def diff(o1: Object, o2: Object): Diff = {
    require(o1.getClass() == o2.getClass())
    diff(new Diff(null, "."), o1, o2)
  }
  
  private def diff(root: Diff, o1: Object, o2: Object): Diff = {
    new FieldTraverser().walk(o1.getClass(),
      f => 
        if (f.get(o1) != f.get(o2)) {
          val d = new Diff(root, f.getName)
          if (descStrategy.shouldProceed(f.getDeclaringClass())) {
            diff(d, f.get(o1), f.get(o2))
          }
        }
    )
    root
  }
}