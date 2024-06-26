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
package org.feijoas.mango.common.collect.immutable

import com.google.common.collect.ImmutableRangeSet
import com.google.common.collect.{RangeSet => GuavaRangeSet}
import org.feijoas.mango.common.annotations.Beta
import org.feijoas.mango.common.collect
import org.feijoas.mango.common.collect.AsOrdered
import org.feijoas.mango.common.collect.Range
import org.feijoas.mango.common.collect.Range.asGuavaRangeConverter
import org.feijoas.mango.common.collect.RangeSetFactory
import org.feijoas.mango.common.collect.RangeSetWrapperLike

import scala.collection.mutable.Builder

/** An immutable implementation of RangeSet that delegates to Guava ImmutableRangeSet
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
@Beta
@SerialVersionUID(1L)
private[mango] class ImmutableRangeSetWrapper[C, O <: Ordering[C]] private (guava: GuavaRangeSet[AsOrdered[C]])(implicit
  override val ordering: O
) extends RangeSet[C, O]
    with RangeSetWrapperLike[C, O, ImmutableRangeSetWrapper[C, O]]
    with Serializable {

  override def delegate = guava
  override def factory: GuavaRangeSet[AsOrdered[C]] => ImmutableRangeSetWrapper[C, O] = new ImmutableRangeSetWrapper(_)(
    ordering
  )
  override def newBuilder = ImmutableRangeSetWrapper.newBuilder[C, O](ordering)
}

/** Factory for ImmutableRangeSetWrapper
 */
private[mango] object ImmutableRangeSetWrapper extends RangeSetFactory[ImmutableRangeSetWrapper] {

  /** Factory method */
  private[mango] def apply[C, O <: Ordering[C]](guava: GuavaRangeSet[AsOrdered[C]])(implicit ord: O) =
    new ImmutableRangeSetWrapper(guava)(ord)

  /** Returns a [[RangeSet]] initialized with the ranges in the specified range set.
   */
  override def apply[C, O <: Ordering[C]](rangeSet: collect.RangeSet[C, O])(implicit ord: O) = rangeSet match {
    case same: ImmutableRangeSetWrapper[C, O] => same
    case _                                    => super.apply(rangeSet)
  }

  /** Returns a new builder for a range set.
   */
  def newBuilder[C, O <: Ordering[C]](implicit ord: O) = new Builder[Range[C, O], ImmutableRangeSetWrapper[C, O]]() {
    var builder = ImmutableRangeSet.builder[AsOrdered[C]]()
    override def addOne(range: Range[C, O]): this.type = {
      builder.add(range.asJava)
      this
    }
    override def clear() = builder = ImmutableRangeSet.builder[AsOrdered[C]]()
    override def result() = new ImmutableRangeSetWrapper(builder.build)
  }
}
