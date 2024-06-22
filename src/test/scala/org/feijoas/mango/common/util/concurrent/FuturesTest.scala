/*
 * Copyright (C) 2013 The Mango Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The code of this project is a port of (or wrapper around) the Guava-libraries.
 *    See http://code.google.com/p/guava-libraries/
 *
 * @author Markus Schneider
 */
package org.feijoas.mango.common.util.concurrent

import java.util.concurrent.{Callable, CountDownLatch, Executors, TimeUnit}
import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.MILLISECONDS
import scala.util.{Failure, Success, Try}
import org.feijoas.mango.common.util.concurrent.Futures.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import com.google.common.util.concurrent.{
  FutureCallback,
  Futures as GuavaFutures,
  ListenableFuture,
  ListenableFutureTask
}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.duration.Duration

/**
 * Tests for [[Future]] helper
 *
 *  @author Markus Schneider
 *  @since 0.7
 */
class FuturesTest extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with MockitoSugar {

  behavior of "ListenableFuture wrapper"

  it should "should throw a timeout exception if time expires on a call on get" in {
    val scalaFuture: Future[Int] = Future { Thread.sleep(1000); fail() }
    val listFut: ListenableFuture[Int] = scalaFuture.asJava

    try {
      listFut.get(100, TimeUnit.MILLISECONDS)
      fail()
    } catch {
      case _: TimeoutException => // latch is still on hold
    }
  }

  it should "return the correct value" in {
    val scalaFuture = Future { 5 }
    val listFut: ListenableFuture[Int] = scalaFuture.asJava

    listFut.get(100, TimeUnit.MILLISECONDS) should be(5)
  }

  it should "call onSuccess if it succeeds" in {
    val finished = new CountDownLatch(1)
    val start = new CountDownLatch(1)
    val scalaFuture = Future { start.await(); 5 }
    val listFut: ListenableFuture[Int] = scalaFuture.asJava

    val callback: Try[Int] => Any = {
      case Success(_) => finished.countDown()
      case _          => fail()
    }

    GuavaFutures.addCallback(
      listFut,
      new FutureCallback[Int] {
        override def onSuccess(result: Int): Unit = {
          callback(Success(result)); ()
        }

        override def onFailure(t: Throwable): Unit = {
          callback(Failure(t)); ()
        }
      },
      Executors.newFixedThreadPool(10).execute(_)
    )

    // start future
    start.countDown()

    // check if callback was called
    finished.await(100, TimeUnit.MILLISECONDS)

    // check if result is correct
    listFut.get(100, TimeUnit.MILLISECONDS) should be(5)
  }

  it should "call onFailure if it fails" in {
    val finished = new CountDownLatch(1)
    val start = new CountDownLatch(1)
    val scalaFuture: Future[Int] = Future { start.await(); fail() }
    val listFut: ListenableFuture[Int] = scalaFuture.asJava

    val callback: Try[Int] => Any = {
      case Failure(_) => finished.countDown()
      case _          => fail()
    }

    GuavaFutures.addCallback(listFut, callback.asJava, Executors.newFixedThreadPool(10).execute(_))
    start.countDown()

    // check if callback was called
    finished.await(100, TimeUnit.MILLISECONDS)
  }

  behavior of "ListenableFuture wrapper if a value is retrieved"

  it should "return the correct Option[Success] if the calculation succeeds" in {
    val latch = new CountDownLatch(1)
    val delegate = createFuture(1, latch, throwException = false)
    val future: Future[Int] = delegate.asScala

    execute(delegate)
    future.value should be(None)

    // finish calculation
    latch.countDown()

    // wait and check result
    Await.result(future, Duration(100, MILLISECONDS)) should be(1)
    future.value should be(Some(Success(1)))
  }

  it should "return the correct Option[Failure] if the calculation fails" in {
    val latch = new CountDownLatch(1)
    val delegate = createFuture(1, latch, throwException = true)
    val future: Future[Int] = delegate.asScala

    execute(delegate)
    future.value should be(None)

    // finish calculation
    latch.countDown()

    // wait and check result
    try {
      Await.result(future, Duration(100, MILLISECONDS)) should be(Some(Failure))
    } catch {
      case e: Exception => e.getCause should be(calculationException)
    }
  }

  behavior of "ListenableFuture wrapper if a callback is registered"

  it should "call onSuccess if successful" in {
    val delegate = createFuture(1, new CountDownLatch(0), throwException = false)
    val future: Future[Int] = delegate.asScala

    // we use this latch to wait until the successCallback
    // is finished
    val successLatch = new CountDownLatch(2)
    val successCallback: PartialFunction[Any, Unit] = { case _ => successLatch.countDown() }

    val completeCallback: Try[Int] => Any = {
      case Success(v) => successCallback(v)
      case Failure(_) => fail()
    }

    future.onComplete {
      case Success(_) => successLatch.countDown()
      case Failure(_) => fail()
    }

    future.onComplete(completeCallback)

    // wait until the future is ready
    execute(delegate)
    Await.ready(future, Duration(100, MILLISECONDS))

    // check number of invocations
    successLatch.await(100, TimeUnit.MILLISECONDS)
    successLatch.getCount should be(0)
  }

  it should "call onFailure if failed" in {
    val delegate = createFuture(1, new CountDownLatch(0), throwException = true)
    val future: Future[Int] = delegate.asScala

    // we use this latch to wait until the successCallback
    // is finished
    val failureLatch = new CountDownLatch(2)
    val failureCallback: PartialFunction[Any, Unit] = { case _ => failureLatch.countDown() }
    val completeCallback: Try[Int] => Any = {
      case Success(_) => fail()
      case Failure(v) => failureCallback(v)
    }

    future.onComplete {
      case Success(_) => fail()
      case Failure(_) => failureLatch.countDown()
    }

    future.onComplete(completeCallback)

    // wait until the future is ready
    execute(delegate)
    Await.ready(future, Duration(100, MILLISECONDS))

    // check number of invocations
    failureLatch.await(100, TimeUnit.MILLISECONDS)
    failureLatch.getCount should be(0)
  }

  behavior of "ListenableFuture wrapper if we are waiting until it's ready"

  it should "call wait until computation is finished" in {
    val latch = new CountDownLatch(1)
    val delegate = createFuture(1, latch, throwException = false)
    val future: Future[Int] = delegate.asScala

    execute(delegate)
    delegate.isDone should be(false)
    future.isCompleted should be(false)

    // finish calculation
    latch.countDown()

    // wait and check result
    Await.ready(future, Duration(100, MILLISECONDS))
    delegate.isDone should be(true)
    future.isCompleted should be(true)
    future.value should be(Some(Success(1)))

    // repeat
    delegate.isDone should be(true)
    future.isCompleted should be(true)
    future.value should be(Some(Success(1)))
  }

  it should "throw an exception if the computation takes too long" in {
    val latch = new CountDownLatch(1)
    val delegate = createFuture(1, latch, throwException = false)
    val future: Future[Int] = delegate.asScala

    execute(delegate)
    delegate.isDone should be(false)
    future.isCompleted should be(false)

    // calculation will need 5 minutes but we wait only 100 ms
    try {
      Await.ready(future, Duration(100, MILLISECONDS))
      fail()
    } catch {
      case _: TimeoutException => // expected
    }
    delegate.isDone should be(false)
    future.isCompleted should be(false)
  }

  val calculationException = new Exception("FutureTest-Exception")

  it should "Await.ready(future,..) must not throw an exception if the Future does" in {
    val latch = new CountDownLatch(1)
    val delegate = createFuture(1, latch, throwException = true)
    val future: Future[Int] = delegate.asScala

    execute(delegate)
    delegate.isDone should be(false)
    future.isCompleted should be(false)

    // finish calculation
    latch.countDown()

    // wait and check result
    Await.ready(future, Duration(100, MILLISECONDS))
    delegate.isDone should be(true)
    future.isCompleted should be(true)
    // Some(Failure(ExecutionException)))
    future.value.get.failed.get.getCause should be(calculationException)
  }

  def execute(command: Runnable): Unit = Executors.newFixedThreadPool(10).execute(command)

  def createFuture[T](value: T, latch: CountDownLatch, throwException: Boolean): ListenableFutureTask[T] = {
    ListenableFutureTask.create(new Callable[T]() {
      override def call(): T = {
        latch.await(5000, TimeUnit.MILLISECONDS)
        if (throwException) {
          throw calculationException
        }
        value
      }
    })
  }
}
