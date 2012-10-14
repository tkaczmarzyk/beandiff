package org.beandiff.display

import org.beandiff.core.Diff

trait DiffPresenter {

  def present(d: Diff): String
}