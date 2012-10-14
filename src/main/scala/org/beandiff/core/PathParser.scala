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