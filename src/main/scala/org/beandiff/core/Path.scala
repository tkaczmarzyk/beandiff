package org.beandiff.core

object Path {

  /**
   * Parses path from string input. Example argument:
   * 
   * {@code nicknames[0].shortVersion}
   * 
   * @param pathStr string representation to be parsed
   * @return an instance of {@link Path}
   */
  def of(pathStr: String): Path = {
    new PathParser().parsePath(pathStr)
  }

}

class Path(val head: Property, val tail: Path) {

  def this(head: Property) = {
    this(head, null)
  }

  def depth: Int = {
    if (tail == null) 1
    else 1 + tail.depth
  }

  def withIndex(i: Int): Path = step(new IndexProperty(i))

  def step(p: Property): Path = {
    if (tail == null)
      new Path(head, new Path(p, null))
    else
      new Path(head, tail.step(p))
  }

  def step(props: List[Property]): Path = {
    if (props.isEmpty)
      this
    else
      this.step(props.head).step(props.tail)
  }

  def last: Property = {
    if (tail == null)
      head
    else tail.last
  }

  override def equals(o: Any): Boolean = {
    o match {
      case that: Path => this.head == that.head && this.tail == that.tail
      case _ => false
    }
  }

  override def toString() = {
    head.toString + {
      if (tail != null) tail.toString
      else ""
    }
  }
}