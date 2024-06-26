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

import com.google.common.collect.{DiscreteDomain => GuavaDiscreteDomain}
import com.google.common.testing.SerializableTester.reserializeAndAssert
import org.feijoas.mango.common.collect.DiscreteDomain.IntDomain
import org.feijoas.mango.common.collect.DiscreteDomain.LongDomain
import org.scalacheck.Arbitrary
import org.scalacheck.Shrink
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/**
 * Tests for [[Range]]
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
class DiscreteDomainTest extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with DiscreteDomainBehaviors {

  ("IntDomain" should behave).like(guavaDomain(IntDomain, GuavaDiscreteDomain.integers()))
  ("LongDomain" should behave).like(guavaDomain(LongDomain, GuavaDiscreteDomain.longs()))

}

private[mango] trait DiscreteDomainBehaviors extends ScalaCheckPropertyChecks with Matchers {
  this: AnyFlatSpec =>

  def guavaDomain[C <: Comparable[?], T: Arbitrary: Shrink](domain: DiscreteDomain[T], guava: GuavaDiscreteDomain[C])(
    implicit view: T => C
  ): Unit = {

    it should "implement distance" in {
      forAll { (start: T, end: T) =>
        domain.distance(start, end) should be(guava.distance(start, end))
      }
    }

    it should "implement next" in {
      forAll { (value: T) =>
        val gn = guava.next(value)
        if (gn == null)
          domain.next(value) should be(None)
        else
          domain.next(value) should be(Some(gn))
      }
    }

    it should "implement previous" in {
      forAll { (value: T) =>
        val gn = guava.previous(value)
        if (gn == null)
          domain.previous(value) should be(None)
        else
          domain.previous(value) should be(Some(gn))
      }
    }

    it should "implement minValue and maxValue" in {
      domain.maxValue() should be(Option(guava.maxValue()))
      domain.minValue() should be(Option(guava.minValue()))
    }

    it should "be serializable" in {
      reserializeAndAssert(domain)
    }
  }
}
