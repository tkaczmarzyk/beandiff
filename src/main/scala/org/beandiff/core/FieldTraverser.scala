package org.beandiff.core

import java.lang.reflect.Field


object FieldTraverser {

  def walk(src: Any)(callback: Field => Unit) = {
    src.getClass().getDeclaredFields() foreach {
      f =>
        f.setAccessible(true)
        callback(f)
    }
  }
}