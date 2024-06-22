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
package org.feijoas.mango.common.cache

import org.feijoas.mango.common.cache.RemovalListener.*
import org.scalatest.flatspec.AnyFlatSpec
import com.google.common.cache.RemovalListener as GuavaRemovalListener
import com.google.common.cache.RemovalNotification as GuavaRemovalNotification
import com.google.common.cache.RemovalCause as GuavaRemovalCause
import org.feijoas.mango.common.cache.RemovalCause.*
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers.*

/**
 * Tests for [[RemovalListener]]
 *
 *  @author Markus Schneider
 *  @since 0.7
 */
class RemovalListenerTest extends AnyFlatSpec {

  behavior of "RemovalListener"

  it should "convert a function to a Guava RemovalListener" in {
    // check for all causes, keys, values
    val causes = List(Collected, Expired, Explicit, Replaced, Size)
    val keys = List(null, "key")
    val values = List(null, "values")

    for {
      key <- keys
      value <- values
      cause <- causes
    } check(key, value, cause)
  }

  def check(key: String, value: String, cause: RemovalCause): Assertion = {
    // assert that the call to the Guava listener was forwarded to listener
    var called = false

    // create a which checks that the notification is the one we give
    // to the Guava listener
    val listener = (n: RemovalNotification[String, String]) => {
      n should be(RemovalNotification(Option(key), Option(value), cause))
      called should be(false)
      called = true
    }

    // implicit conversion
    val guavaListener: GuavaRemovalListener[String, String] = listener.asJava
    val notification = guavaNotification(key, value, cause.asJava)

    // the call forwards to listener
    guavaListener.onRemoval(notification)
    called should be(true)
  }

  /**
   * create a guava GuavaRemovalNotification via reflection
   */
  def guavaNotification[K, V](key: K, value: V, cause: GuavaRemovalCause): GuavaRemovalNotification[K, V] = {
    val ctors = classOf[GuavaRemovalNotification[K, V]].getDeclaredConstructors
    ctors.length should be(1)
    val ctor = ctors(0)
    ctor.setAccessible(true)
    ctor
      .newInstance(key.asInstanceOf[Object], value.asInstanceOf[Object], cause)
      .asInstanceOf[GuavaRemovalNotification[K, V]]
  }
}
