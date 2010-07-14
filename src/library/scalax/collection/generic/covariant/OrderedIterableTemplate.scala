/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: Iterable.scala 15188 2008-05-24 15:01:02Z stepancheg $


package scalax.collection.generic.covariant

import annotation.unchecked.uncheckedVariance

trait OrderedIterableTemplate[+CC[+B] <: OrderedIterable[B] with OrderedIterableTemplate[CC, B], +A] 
  extends generic.OrderedIterableTemplate[CC, A @uncheckedVariance] {self /*: CC[A]*/ => }
