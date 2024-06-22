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
package org.feijoas.mango.common.base

import org.feijoas.mango.common.base.Preconditions.checkArgument
import org.feijoas.mango.common.base.Preconditions.checkElementIndex
import org.feijoas.mango.common.base.Preconditions.checkNotNull
import org.feijoas.mango.common.base.Preconditions.checkPositionIndex
import org.feijoas.mango.common.base.Preconditions.checkPositionIndexes
import org.feijoas.mango.common.base.Preconditions.checkState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Tests for [[Preconditions]]
 *
 *  @author Markus Schneider
 *  @since 0.7 (copied from guava-libraries)
 */
class PreconditionsTest extends AnyFlatSpec with Matchers {

  val IGNORE_ME: Any = new Object() {
    override def toString: String = {
      fail()
    }
  }

  "checkArgument called with true" should "success" in {
    checkArgument(expression = true)
  }

  "checkArgument called with false" should "fail" in {
    an[IllegalArgumentException] should be thrownBy checkArgument(expression = false)
  }

  "checkArgument called with true" should "ignore the second arg" in {
    checkArgument(expression = true, IGNORE_ME)
  }

  "checkArgument" should "put the msg into exception" in {
    (the[IllegalArgumentException] thrownBy {
      checkArgument(expression = false, new Message())
    } should have).message("A message")
  }

  "checkArgument" should "put 'null' into exception if called with null" in {
    (the[IllegalArgumentException] thrownBy {
      checkArgument(expression = false, null)
    } should have).message("null")
  }

  "checkArgument called with true" should "ignore the third arg" in {
    checkArgument(expression = true, "%s", IGNORE_ME)
  }

  "checkArgument" should "format message" in {
    val format = "I ate %s pies."
    (the[IllegalArgumentException] thrownBy {
      checkArgument(expression = false, format, 5)
    } should have).message("I ate 5 pies.")
  }

  "checkState called with true" should "success" in {
    checkState(true)
  }

  "checkState called with false" should "fail" in {
    an[IllegalStateException] should be thrownBy checkState(false)
  }

  "checkState called with true" should "ignore the second arg" in {
    checkState(expression = true, IGNORE_ME)
  }

  "checkState" should "put the msg into exception" in {
    (the[IllegalStateException] thrownBy {
      checkState(expression = false, new Message())
    } should have).message("A message")
  }

  "checkState" should "put 'null' into exception if called with null" in {
    (the[IllegalStateException] thrownBy {
      checkState(expression = false, null)
    } should have).message("null")
  }

  "checkState called with true" should "ignore the third arg" in {
    checkState(expression = true, "%s", IGNORE_ME)
  }

  "checkState" should "format message" in {
    val format = "I ate %s pies."
    (the[IllegalStateException] thrownBy {
      checkState(expression = false, format, 5)
    } should have).message("I ate 5 pies.")
  }

  val NON_NULL_STRING = "foo"

  "checkNotNull" should "return non-null string" in {
    checkNotNull(NON_NULL_STRING) should equal(NON_NULL_STRING)
  }

  "checkNotNull" should "should fail on null" in {
    an[NullPointerException] should be thrownBy checkNotNull(null)
  }

  "successful checkNotNull" should "ignore the second arg" in {
    checkNotNull(NON_NULL_STRING, IGNORE_ME) should equal(NON_NULL_STRING)
  }

  "checkNotNull" should "put the msg into exception" in {
    (the[NullPointerException] thrownBy {
      checkNotNull(null, new Message())
    } should have).message("A message")
  }

  "successful checkNotNull" should "ignore the third arg" in {
    checkNotNull(NON_NULL_STRING, "%s", IGNORE_ME) should equal(NON_NULL_STRING)
  }

  "checkNotNull" should "format message" in {
    val format = "I ate %s pies."
    (the[NullPointerException] thrownBy {
      checkNotNull(null, format, 5)
    } should have).message("I ate 5 pies.")
  }

  "checkElementIndex" should "return the index on success" in {
    checkElementIndex(0, 1) should be(0)
    checkElementIndex(0, 2) should be(0)
    checkElementIndex(1, 3) should be(1)
  }

  "checkElementIndex" should "fail if size is neg" in {
    an[IllegalArgumentException] should be thrownBy checkElementIndex(1, -1)
  }

  "checkElementIndex" should "fail if index is neg" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkElementIndex(-1, 1)
    } should have).message("index (-1) must not be negative")
  }

  "checkElementIndex" should "fail index is too high " in {
    an[IndexOutOfBoundsException] should be thrownBy checkElementIndex(1, 1)
  }

  "checkElementIndex with desc" should "fail if size is neg" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkElementIndex(-1, 1, "desc")
    } should have).message("desc (-1) must not be negative")
  }

  "checkElementIndex with desc" should "fail index is too high " in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkElementIndex(1, 1, "desc")
    } should have).message("desc (1) must be less than size (1)")
  }

  "checkPositionIndex" should "return the index on success" in {
    checkPositionIndex(0, 0) should be(0)
    checkPositionIndex(0, 1) should be(0)
    checkPositionIndex(1, 1) should be(1)
  }

  "checkPositionIndex" should "fail if size is neg" in {
    an[IllegalArgumentException] should be thrownBy checkPositionIndex(1, -1)
  }

  "checkPositionIndex" should "fail if index is neg" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndex(-1, 1)
    } should have).message("index (-1) must not be negative")
  }

  "checkPositionIndex" should "fail if index is too high" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndex(2, 1)
    } should have).message("index (2) must not be greater than size (1)")
  }

  "checkPositionIndex with desc" should "fail if index is neg" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndex(-1, 1, "desc")
    } should have).message("desc (-1) must not be negative")
  }

  "checkPositionIndex with desc" should "fail if index is too high" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndex(2, 1, "desc")
    } should have).message("desc (2) must not be greater than size (1)")
  }

  "checkPositionIndexes" should "pass with good values" in {
    checkPositionIndexes(0, 0, 0)
    checkPositionIndexes(0, 0, 1)
    checkPositionIndexes(0, 1, 1)
    checkPositionIndexes(1, 1, 1)
  }

  "checkPositionIndexes" should "fail if size is neg" in {
    an[IllegalArgumentException] should be thrownBy checkPositionIndexes(1, 1, -1)
  }

  "checkPositionIndexes" should "fail if start is neg" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndexes(-1, 1, 1)
    } should have).message("start index (-1) must not be negative")
  }

  "checkPositionIndexes" should "fail if end is too high" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndexes(0, 2, 1)
    } should have).message("end index (2) must not be greater than size (1)")
  }

  "checkPositionIndexes" should "fail if start > end" in {
    (the[IndexOutOfBoundsException] thrownBy {
      checkPositionIndexes(1, 0, 1)
    } should have).message("end index (0) must not be less than start index (1)")
  }

  "vararg functions" should "work with AnyVal and AnyRef" in {
    val value: AnyVal = 1
    val ref: AnyRef = new Object
    // this must compile
    checkArgument(expression = true, "", value, ref, value)
    checkArgument(expression = true, "", ref, ref, value)
    checkNotNull(new Object, "", value, ref, value)
    checkNotNull(new Object, "", ref, ref, value)
    checkState(true, "", value, ref, value)
  }

  class Message(var invoked: Boolean = false) {
    override def toString: String = {
      invoked should be(false)
      invoked = true
      "A message"
    }
  }
}
