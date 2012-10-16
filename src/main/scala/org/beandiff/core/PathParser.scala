/**
 * Copyright (c) 2012, Tomasz Kaczmarzyk.
 * 
 * This file is part of BeanDiff.
 * 
 * BeanDiff is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
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
package org.beandiff.core

import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator.RegexParsers

class PathParser extends RegexParsers with JavaTokenParsers {

  def fieldProperty: Parser[FieldProperty] = {
    ident ^^ {
      new FieldProperty(_)
    }
  }

  def indexProperty: Parser[IndexProperty] = {
    "[" ~> positiveInt <~ "]" ^^ (new IndexProperty(_))
  }
  
  def property: Parser[Property] = {
    indexProperty | fieldProperty
  }
  
  def nestedProperty: Parser[Property] = {
    (fieldSeparator ~> fieldProperty) | indexProperty
  }
  
  def fieldSeparator: Parser[String] = Path.FieldSeparator
    
  def positiveInt: Parser[Int] = {
    """\d+""".r ^^ (_.toInt)
  }

  def path: Parser[Path] = {
    (property) ~ rep(nestedProperty) ^^ {
      x =>
        new Path(x._1).step(x._2)
    }
  }
  
  def parsePath(input: String): Path = {
    parseAll(path, input).get
  }
}