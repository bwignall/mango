package org.feijoas.mango.common.base

import org.feijoas.mango.common.base.Equivalence.asGuavaEquiv
import org.feijoas.mango.common.base.Equivalence.asMangoEquiv
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import com.google.common.base as gcm
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** Tests for [[Equiv]]
 *
 *  @author Markus Schneider
 *  @since 0.10
 */
class EquivalenceTest extends AnyFreeSpec with ScalaCheckPropertyChecks {

  "AsMangoEquiv" - {
    "should forward equiv to Guava" in {
      val mango: Equiv[Int] = SignumEquiv
      val guava: gcm.Equivalence[Int] = mango.asJava
      forAll { (x: Int, y: Int) =>
        guava.equivalent(x, y) should be(mango.equiv(x, y))
        guava.equivalent(x, x) should be(mango.equiv(x, x))
      }
    }
    "it should not wrap an Equiv twice" in {
      val mango: Equiv[Int] = SignumEquiv
      val guava: gcm.Equivalence[Int] = mango.asJava
      val wrappedAgain: Equiv[Int] = guava.asScala

      mango should be(wrappedAgain)
    }
  }

  "AsGuavaEquiv" - {
    "should forward equiv to Mango" in {
      val guava: gcm.Equivalence[Int] = SignumEquivalence
      val mango: Equiv[Int] = guava.asScala
      forAll { (x: Int, y: Int) =>
        mango.equiv(x, y) should be(guava.equivalent(x, y))
        mango.equiv(x, x) should be(guava.equivalent(x, x))
      }
    }
    "it should not wrap an Equiv twice" in {
      val guava: gcm.Equivalence[Int] = SignumEquivalence
      val mango: Equiv[Int] = guava.asScala
      val wrappedAgain: gcm.Equivalence[Int] = mango.asJava

      guava should be(wrappedAgain)
    }
  }
}

private[mango] object SignumEquiv extends Equiv[Int] {
  def equiv(x: Int, y: Int) = x.sign == y.sign
}

private[mango] object SignumEquivalence extends gcm.Equivalence[Int] {
  def doEquivalent(x: Int, y: Int) = x.sign == y.sign
  def doHash(x: Int) = 2 * x
}
