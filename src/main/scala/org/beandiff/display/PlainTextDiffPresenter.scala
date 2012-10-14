package org.beandiff.display

import org.beandiff.core.Diff
import org.beandiff.core.EmptyPath
import org.beandiff.core.LeafDiff
import org.beandiff.core.Path

class PlainTextDiffPresenter(
    private val pathValueSeparator: String = " -- ",
    private val valuesSeparator: String = " vs ",
    private val valueQuote: String = "'",
    private val differenceSeparator: String = "\n") extends DiffPresenter {

  
  def present(d: Diff): String = present(EmptyPath, d)
  
  private def present(currentPath: Path, d: Diff): String = {
    if (!d.hasDifference)
      ""
    else {
      val result = new StringBuilder
      
      for ((property, diff) <- d.diffs) {
        if (diff.isInstanceOf[LeafDiff]) { //FIXME avoid direct type-checking!
          result.append(currentPath.step(property)).append(pathValueSeparator)
          result.append(valueQuote).append(diff.o1).append(valueQuote)
          result.append(valuesSeparator)
          result.append(valueQuote).append(diff.o2).append(valueQuote)
          result.append(differenceSeparator)
        } else {
          result.append(present(currentPath.step(property), diff))
        }
      }
      
      result.toString
    }
  }
}