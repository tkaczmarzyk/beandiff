package org.beandiff.core

import scala.collection.mutable.HashMap

class PropertyHandlerDictionary(val defaultHandler: PropertyHandler) {

  private val handlerMap = new HashMap[Class[_], PropertyHandler]
  
  def this(defaultHandler: PropertyHandler, handlers: (Class[_], PropertyHandler)*) = {
    this(defaultHandler)
    handlers.foreach(handlerMap += _)
  }
  
  def apply(c: Class[_]) = {
    if (handlerMap.contains(c))
      handlerMap(c)
    else
      defaultHandler
  }
}