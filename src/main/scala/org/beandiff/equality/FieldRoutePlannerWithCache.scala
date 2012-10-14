package org.beandiff.equality

import org.beandiff.core.FieldRoutePlanner
import scala.collection.mutable.HashMap
import java.lang.reflect.Field

object FieldRoutePlannerWithCache extends FieldRoutePlanner {

  private val fields = new HashMap[Class[_], Array[Field]]

  override final def getDeclaredFields(c: Class[_]) = {
    if (!fields.contains(c))
      fields += (c -> super.getDeclaredFields(c))

    fields(c)
  }
}