/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: RichString.scala 15602 2008-07-24 08:20:38Z washburn $


package scalax.runtime

import scala.util.matching.Regex
import scalax.collection._
import scalax.collection.mutable.StringBuilder
import scalax.collection.generic
import scalax.collection.generic._

object BoxedString {
  // just statics for rich string.
  private final val LF: Char = 0x0A
  private final val FF: Char = 0x0C
  private final val CR: Char = 0x0D
  private final val SU: Char = 0x1A
}

import BoxedString._

class BoxedString(val self: String) extends StringVector[Char] with Proxy with PartialFunction[Int, Char] with Ordered[String] {

  /** Return element at index `n`
   *  @throws   IndexOutofBoundsException if the index is not valid
   */
  def apply(n: Int): Char = self charAt n

  def length = self.length

  override def unbox = self
  override def mkString = self
  override def toString = self

  /** return n times the current string 
   */
  def * (n: Int): String = {
    val buf = new StringBuilder
    for (i <- 0 until n) buf append self
    buf.toString
  }

  override def compare(other: String) = self compareTo other

  private def isLineBreak(c: Char) = c == LF || c == FF

  /** <p>
   *    Strip trailing line end character from this string if it has one.
   *    A line end character is one of
   *  </p>
   *  <ul style="list-style-type: none;">
   *    <li>LF - line feed   (0x0A hex)</li>
   *    <li>FF - form feed   (0x0C hex)</li>
   *  </ul>
   *  <p>
   *    If a line feed character LF is preceded by a carriage return CR
   *    (0x0D hex), the CR character is also stripped (Windows convention).
   *  </p>
   */
  def stripLineEnd: String = {
    val len = self.length
    if (len == 0) self
    else {
      val last = apply(len - 1)
      if (isLineBreak(last))
        self.substring(0, if (last == LF && len >= 2 && apply(len - 2) == CR) len - 2 else len - 1)
      else 
        self
    }
  }

  /** <p>
   *    Return all lines in this string in an iterator, including trailing
   *    line end characters.
   *  </p>
   *  <p>
   *    The number of strings returned is one greater than the number of line
   *    end characters in this string. For an empty string, a single empty
   *    line is returned. A line end character is one of
   *  </p>
   *  <ul style="list-style-type: none;">
   *    <li>LF - line feed   (0x0A hex)</li>
   *    <li>FF - form feed   (0x0C hex)</li>
   *  </ul>
   */
  def linesWithSeparators = new Iterator[String] {
    val len = self.length
    var index = 0
    def hasNext: Boolean = index < len
    def next(): String = {
      if (index >= len) throw new NoSuchElementException("next on empty iterator")
      val start = index
      while (index < len && !isLineBreak(apply(index))) index += 1
      index += 1
      self.substring(start, index min len)
    }
  }

  /** Return all lines in this string in an iterator, excluding trailing line
   *  end characters, i.e. apply <code>.stripLineEnd</code> to all lines
   *  returned by <code>linesWithSeparators</code>.
   */
  def lines: Iterator[String] = 
    linesWithSeparators map (line => new BoxedString(line).stripLineEnd)

  /** Returns this string with first character converted to upper case */
  def capitalize: String =
    if (self == null) null
    else if (self.length == 0) ""
    else {
      val chars = self.toCharArray
      chars(0) = chars(0).toUpperCase
      new String(chars)
    }

  /** <p>
   *    For every line in this string:
   *  </p>
   *  <blockquote>
   *     Strip a leading prefix consisting of blanks or control characters
   *     followed by <code>marginChar</code> from the line.
   *  </blockquote>
   */
  def stripMargin(marginChar: Char): String = {
    val buf = new StringBuilder
    for (line <- linesWithSeparators) {
      val len = line.length
      var index = 0
      while (index < len && line.charAt(index) <= ' ') index += 1
      buf append
        (if (index < len && line.charAt(index) == marginChar) line.substring(index + 1) else line)
    }
    buf.toString
  }

  /** <p>
   *    For every line in this string:
   *  </p>
   *  <blockquote>
   *     Strip a leading prefix consisting of blanks or control characters
   *     followed by <code>|</code> from the line.
   *  </blockquote>
   */
  def stripMargin: String = stripMargin('|')

  // NB. "\\Q" + '\\' + "\\E" works on Java 1.5 and newer, but not on Java 1.4
  private def escape(ch: Char): String = ch match {
    case '\\' => "\\\\"
    case _ => "\\Q"+ch+"\\E"
  }

  @throws(classOf[java.util.regex.PatternSyntaxException])
  def split(separator: Char): Array[String] = self.split(escape(separator))

  @throws(classOf[java.util.regex.PatternSyntaxException])
  def split(separators: Array[Char]): Array[String] = {
    val re = separators.foldLeft("[")(_+escape(_)) + "]"
    self.split(re)
  }

  /** You can follow a string with `.r', turning
   *  it into a Regex. E.g.
   *
   *  """A\w*""".r   is the regular expression for identifiers starting with `A'.
   */
  def r: Regex = new Regex(self)
  
  def toBoolean: Boolean = parseBoolean(self)
  def toByte: Byte       = java.lang.Byte.parseByte(self)
  def toShort: Short     = java.lang.Short.parseShort(self)
  def toInt: Int         = java.lang.Integer.parseInt(self)
  def toLong: Long       = java.lang.Long.parseLong(self)
  def toFloat: Float     = java.lang.Float.parseFloat(self)
  def toDouble: Double   = java.lang.Double.parseDouble(self)

  private def parseBoolean(s: String): Boolean =
    if (s != null) s.toLowerCase match {
      case "true" => true
      case "false" => false
      case _ => throw new NumberFormatException("For input string: \""+s+"\"")
    }
    else
      throw new NumberFormatException("For input string: \"null\"")

  def toArray: Array[Char] = {
    val result = new Array[Char](length)
    self.getChars(0, length, result, 0)
    result
  }
}

