package org.beandiff.display

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.beandiff.core.Diff
import org.beandiff.core.LeafDiff
import org.beandiff.core.Path
import org.scalatest.matchers.ShouldMatchers
import org.beandiff.beans.ValueBean
import org.beandiff.beans.IdBean

@RunWith(classOf[JUnitRunner])
class PlainTextDiffPresenterTest extends FunSuite with ShouldMatchers {

  val presenter = new PlainTextDiffPresenter
  
  val bean1 = new ValueBean[IdBean]("Aaa", new IdBean(17))
  val bean2 = new ValueBean[IdBean]("Bbb", new IdBean(8))
  
  val diff1 = new Diff(bean1, bean2) // TODO reduce verbosity and dependency to other functionality
  diff1(Path.of("name")) = new LeafDiff(bean1.name, bean2.name)
  diff1(Path.of("values[0].id")) = new LeafDiff(bean1.values.get(0).id, bean2.values.get(0).id)
  
  
  test("should display simple properties") {
    presenter.present(diff1) should be === "name -- 'Aaa' vs 'Bbb'\n" + "values[0].id -- '17' vs '8'\n"
  }
}