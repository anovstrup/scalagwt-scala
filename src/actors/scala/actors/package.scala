package scala

package object actors {

  // type of Reactors tracked by termination detector
  private[actors] type TrackedReactor = Reactor[A] forSome { type A >: Null }

  @deprecated("use scheduler.ForkJoinScheduler instead")
  type FJTaskScheduler2 = scala.actors.scheduler.ForkJoinScheduler

  @deprecated("use scheduler.ForkJoinScheduler instead")
  type TickedScheduler = scala.actors.scheduler.ForkJoinScheduler

  @deprecated("use scheduler.ForkJoinScheduler instead")
  type WorkerThreadScheduler = scala.actors.scheduler.ForkJoinScheduler

  @deprecated("this class is going to be removed in a future release")
  type WorkerThread = java.lang.Thread

  @deprecated("use scheduler.SingleThreadedScheduler instead")
  type SingleThreadedScheduler = scala.actors.scheduler.SingleThreadedScheduler

  // This used to do a blind cast and throw a CCE after the package
  // object was loaded.  I have replaced with a variation that should work
  // in whatever cases that was working but fail less exceptionally for
  // those not intentionally using it.
  @deprecated("this value is going to be removed in a future release")
  val ActorGC = scala.actors.Scheduler.impl match {
    case x: scala.actors.scheduler.ActorGC  => x
    case _                                  => null
  }
}
