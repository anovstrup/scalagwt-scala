/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2006-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: Vector.scala 15437 2008-06-25 16:22:45Z stepancheg $

package scalax.collection.generic.mutable

import collection.mutable.Vector
import collection.mutable.Vector._

/** Sequences that support O(1) element access and O(1) length computation.
 *  @author Sean McDirmid
 *  @author Martin Odersky
 *  @version 2.8
 */
trait VectorTemplate[+CC[B] <: VectorTemplate[CC, B] with Vector[B], A] extends generic.VectorTemplate[CC, A] { 
self => 

  def update(idx: Int, elem: A)

  /** Creates a view of this iterable @see OrderedIterable.View
   */
  override def view: VectorView[CC, A] = new VectorView[CC, A] { // !!! Martin: We should maybe infer the type parameters here?
    val origin: Vector[_] = thisCC
    val length: Int = self.length
    def apply(idx: Int): A = self.apply(idx)
    def update(idx: Int, elem: A) = self.update(idx, elem)
  }

  /** A sub-sequence view  starting at index `from`
   *  and extending up to (but not including) index `until`.
   *
   *  @param from   The index of the first element of the slice
   *  @param until  The index of the element following the slice
   *  @note  The difference between `view` and `slice` is that `view` produces
   *         a view of the current sequence, whereas `slice` produces a new sequence.
   *
   *  @note view(from, to)  is equivalent to view.slice(from, to)
   */ 
  override def view(from: Int, until: Int): VectorView[CC, A] = view.slice(from, until)

  def readOnly: Sequence[A] = new collection.immutable.Vector[A] { //!!!
    def length = self.length
    def apply(idx : Int) = self.apply(idx)
    def newBuilder[B]: Builder[CC, B] = self.newBuilder[B]
    override def foreach(f: A => Unit) = self.foreach(f)
    override def stringPrefix = self.stringPrefix+"RO"
  }
}

