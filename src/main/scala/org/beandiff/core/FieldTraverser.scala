package org.beandiff.core

import java.lang.reflect.Field


class FieldTraverser {

  def walk(src: Class[_], callback: Field => Unit) = {
    src.getDeclaredFields() foreach {
      f => 
        f.setAccessible(true)
        callback(f)
    }
  }
}