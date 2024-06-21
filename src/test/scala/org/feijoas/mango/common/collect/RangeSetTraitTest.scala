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

import scala.annotation.meta.beanGetter
import scala.annotation.meta.beanSetter
import scala.annotation.meta.field
import scala.annotation.meta.getter
import scala.annotation.meta.setter
import scala.collection.convert.decorateAll.asScalaSetConverter
import scala.collection.mutable.Builder
import scala.math.Ordering.Int

import org.feijoas.mango.common.annotations.Beta
import org.feijoas.mango.common.collect.Range.asGuavaRangeConverter
import org.scalatest.FreeSpec
import org.scalatest.Matchers.be
import org.scalatest.Matchers.convertToAnyShouldWrapper

import com.google.common.collect.ImmutableRangeSet

/** Tests for all default implementations in [[RangeSet]]
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
class RangeSetTraitTest extends FreeSpec with RangeSetBehaviors {

  /** Returns a new builder for a range set.
   */
  def newBuilder[C, O <: Ordering[C]](implicit ord: O) = new Builder[Range[C, O], DummyRangeSet[C, O]]() {
    var guavaBuilder = ImmutableRangeSet.builder[AsOrdered[C]]()
    override def +=(range: Range[C, O]): this.type = {
      guavaBuilder.add(range.asJava)
      this
    }
    override def clear() = guavaBuilder = ImmutableRangeSet.builder[AsOrdered[C]]()
    override def result() = new DummyRangeSet(guavaBuilder.build())
  }

  "trait RangeSet" - {
    behave.like(rangeSet(newBuilder[Int, Int.type]))
  }

  "object RangeSet" - {
    "should return an empty RangeSet if #empty is called" in {
      val emptySet = RangeSet.empty[Int, Int.type]
      emptySet.isEmpty should be(true)
    }
    "should return a new RangeSet with all ranges supplied with #apply(Iterable)" in {
      val set = RangeSet.apply(Range.closed(4, 5))
      set.isEmpty should be(false)
      set.asRanges should be(Set(Range.closed(4, 5)))
    }
    "should return a new builder if #newBuild is called" in {
      // just check if the compiler complains
      val builder: Builder[Range[Int, Int.type], RangeSet[Int, Int.type]] = RangeSet.newBuilder[Int, Int.type]
    }
  }
}

private[mango] class DummyRangeSet[C, O <: Ordering[C]] private[mango] (
  private val rset: ImmutableRangeSet[AsOrdered[C]]
)(implicit protected val ord: O)
    extends RangeSet[C, O] {

  override def span(): Option[Range[C, O]] = rset.isEmpty match {
    case true  => None
    case false => Some(Range(rset.span))
  }

  override def asRanges(): Set[Range[C, O]] = {
    Set.empty ++ rset.asRanges().asScala.view.map(Range[C, O](_))
  }

  override def complement(): RangeSet[C, O] = new DummyRangeSet[C, O](rset.complement())
  override def subRangeSet(view: Range[C, O]) = new DummyRangeSet[C, O](rset.subRangeSet(view.asJava))
  override def newBuilder = throw new NotImplementedError
}
