package org.beandiff.beans

import java.util.ArrayList

class ValueBean[T] {
  var name: String = "[noname]"
  var values: java.util.List[T] = new ArrayList
  
  def this(name: String, vals: T*) = {
    this()
    this.name = name
    for (v <- vals) {
      values.add(v)
    }
  }
  
}