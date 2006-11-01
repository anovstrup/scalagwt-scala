/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2006, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$


package scala.runtime


import Predef.Class

[serializable]
final class BoxedFloatArray(val value: Array[Float]) extends BoxedArray {

  def length: Int = value.length

  def apply(index: Int): AnyRef = BoxedFloat.box(value(index))

  def update(index: Int, elem: AnyRef): Unit = {
    value(index) = elem.asInstanceOf[BoxedNumber].floatValue()
  }

  def unbox(elemTag: String): AnyRef = value
  def unbox(elemClass: Class): AnyRef = value

  override def equals(other: Any) =
    value == other ||
    other.isInstanceOf[BoxedFloatArray] && value == other.asInstanceOf[BoxedFloatArray].value

  override def hashCode(): Int = value.hashCode()
       
  def subArray(start: Int, end: Int): Array[Float] = {
    val result = new Array[Float](end - start)
    Array.copy(value, start, result, 0, end - start)
    result
  }    

  def filter(p: Any => Boolean): Array[Float] = {
    val include = new Array[Boolean](value.length)
    var len = 0
    var i = 0
    while (i < value.length) {
      if (p(value(i))) { include(i) = true; len = len + 1 }
      i = i + 1
    }
    val result = new Array[Float](len)
    len = 0
    i = 0
    while (len < result.length) {
      if (include(i)) { result(len) = value(i); len = len + 1 }
      i = i + 1
    }
    result
  }
}

