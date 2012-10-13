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

/**
 * A container for syntactic sugar methods
 * 
 * @author Tomasz Kaczmarzyk
 */
object BeanDiff {

  val descStrategy = EndOnSimpleTypeStrategy

  def diff(o1: Any, o2: Any): Diff = {
    val d = new Diff()
    
    new ObjectWalker(
      (path, val1, val2, isLeaf) =>
        if (val1 != val2) {
          d(path) = if (isLeaf) new LeafDiff else new Diff()
        }
    ).walk(o1, o2) 
    d
  }
}