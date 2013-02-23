/**
 * Copyright (c) 2012-2013, Tomasz Kaczmarzyk.
 *
 * This file is part of BeanDiff.
 *
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * BeanDiff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BeanDiff; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.beandiff.equality

import org.beandiff.core.model.Path

class SelectiveEqualityInvestigator private () extends BaseEqInvestigator {

  private var paths: Seq[Path] = null

  def this(pathStr: String, pathStrs: String*) = {
    this()
    this.paths = (pathStrs :+ pathStr).map(Path(_))
  }

  def this(path: Path, paths: Path*) = {
    this()
    this.paths = (paths :+ path)
  }

  override def objsAreEqual(o1: Any, o2: Any) = {
    paths.forall((p: Path) =>
      (p.get(o1), p.get(o2)) match {
        case (Some(v1), Some(v2)) => (v1 == v2)
        case _ => false
      })
  }
}