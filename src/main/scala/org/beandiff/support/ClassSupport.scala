package org.beandiff.support

import ClassSupport.convert

object ClassSupport {
  implicit def convert(c: Class[_]): ClassSupport = new ClassSupport(c)
}

class ClassSupport(c: Class[_]) {

  def allSuperTypes: List[Class[_]] = {
    if (c == classOf[Object])
      List()
    else {
      List(c.getSuperclass()) ++ c.getInterfaces() ++
        allSuperInterfaces(c.getInterfaces()) ++
        c.getSuperclass().allSuperTypes
    }
  }

  private def allSuperInterfaces(interfaces: Array[Class[_]]): List[Class[_]] = {
    if (interfaces.isEmpty)
      List()
    else
      interfaces.head.getInterfaces().toList ++ allSuperInterfaces(interfaces.tail)
  }
}