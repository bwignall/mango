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
package org.feijoas.mango.common.collect

import com.google.common.collect.{BoundType => GuavaBoundType}
import com.google.common.testing.SerializableTester
import org.feijoas.mango.common.collect.BoundType.Closed
import org.feijoas.mango.common.collect.BoundType.Open
import org.feijoas.mango.common.collect.BoundType.asGuavaBoundType
import org.feijoas.mango.common.collect.BoundType.asMangoBoundType
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

/** Tests for [[BoundType]]
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
class BoundTypeTest extends AnyFunSuite {

  test("convert from Guava to Mango") {
    val gOpen: GuavaBoundType = GuavaBoundType.OPEN
    val mOpen: BoundType = gOpen.asScala

    val gClosed: GuavaBoundType = GuavaBoundType.CLOSED
    val mClosed: BoundType = gClosed.asScala

    (mOpen should be).theSameInstanceAs(Open)
    (mClosed should be).theSameInstanceAs(Closed)
  }

  test("convert from Mango to Guava") {
    val mOpen: BoundType = Open
    val gOpen: GuavaBoundType = mOpen.asJava

    val mClosed: BoundType = Closed
    val gClosed: GuavaBoundType = mClosed.asJava

    (gOpen should be).theSameInstanceAs(GuavaBoundType.OPEN)
    (gClosed should be).theSameInstanceAs(GuavaBoundType.CLOSED)
  }

  test("throws exception if attempt to convert null") {
    intercept[NullPointerException] {
      val mBT: BoundType = null
      val _: GuavaBoundType = mBT.asJava
    }

    intercept[NullPointerException] {
      val gBT: GuavaBoundType = null
      val _: BoundType = gBT.asScala
    }
  }

  test("serializable") {
    SerializableTester.reserializeAndAssert(Open)
    SerializableTester.reserializeAndAssert(Closed)
  }
}
