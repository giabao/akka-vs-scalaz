package com.softwaremill

import cats.{MonadError, StackSafeMonad}
import zio.ZIO

object IOInstances {
  implicit final def monadErrorInstance[R, E]: MonadError[ZIO[R, E, *], E] =
    monadErrorInstance0.asInstanceOf[MonadError[ZIO[R, E, *], E]]

  private[this] final val monadErrorInstance0: MonadError[ZIO[Any, Any, *], Any] =
    new CatsMonadError[Any, Any]
}

// copy from dev.zio:zio-interop-cats
private class CatsMonadError[R, E] extends MonadError[ZIO[R, E, *], E] with StackSafeMonad[ZIO[R, E, *]] {
  override final def pure[A](a: A): ZIO[R, E, A]                                         = ZIO.succeed(a)
  override final def map[A, B](fa: ZIO[R, E, A])(f: A => B): ZIO[R, E, B]                = fa.map(f)
  override final def flatMap[A, B](fa: ZIO[R, E, A])(f: A => ZIO[R, E, B]): ZIO[R, E, B] = fa.flatMap(f)
  override final def flatTap[A, B](fa: ZIO[R, E, A])(f: A => ZIO[R, E, B]): ZIO[R, E, A] = fa.tap(f)

  override final def widen[A, B >: A](fa: ZIO[R, E, A]): ZIO[R, E, B]                                = fa
  override final def map2[A, B, Z](fa: ZIO[R, E, A], fb: ZIO[R, E, B])(f: (A, B) => Z): ZIO[R, E, Z] = fa.zipWith(fb)(f)
  override final def as[A, B](fa: ZIO[R, E, A], b: B): ZIO[R, E, B]                                  = fa.as(b)
  override final def whenA[A](cond: Boolean)(f: => ZIO[R, E, A]): ZIO[R, E, Unit]                    = ZIO.effectSuspendTotal(f).when(cond)
  override final def unit: ZIO[R, E, Unit]                                                           = ZIO.unit

  override final def handleErrorWith[A](fa: ZIO[R, E, A])(f: E => ZIO[R, E, A]): ZIO[R, E, A] = fa.catchAll(f)
  override final def recoverWith[A](fa: ZIO[R, E, A])(pf: PartialFunction[E, ZIO[R, E, A]]): ZIO[R, E, A] =
    fa.catchSome(pf)
  override final def raiseError[A](e: E): ZIO[R, E, A] = ZIO.fail(e)

  override final def attempt[A](fa: ZIO[R, E, A]): ZIO[R, E, Either[E, A]] = fa.either
}
