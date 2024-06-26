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
package org.feijoas.mango.common.collect.mutable

import org.feijoas.mango.common.annotations.Beta
import org.feijoas.mango.common.collect
import org.feijoas.mango.common.collect.Range
import org.feijoas.mango.common.collect.Range.asGuavaRangeConverter

/** Implementation trait for mutable [[RangeSet]] that delegates to Guava
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
@Beta
private[mango] trait RangeSetWrapperLike[C, O <: Ordering[C], +Repr <: RangeSetWrapperLike[C, O, Repr] with RangeSet[C,
                                                                                                                     O
]] extends collect.RangeSetWrapperLike[C, O, Repr]
    with RangeSet[C, O] {
  self =>

  override def add(range: Range[C, O]): Unit = delegate.add(range.asJava)
  override def remove(range: Range[C, O]): Unit = delegate.remove(range.asJava)
  override def clear(): Unit = delegate.clear()

  override def addAll(other: RangeSet[C, O]): Unit = other match {
    case that: RangeSetWrapperLike[C, O, Repr] => delegate.addAll(that.delegate)
    case _                                     => super.addAll(other)
  }

  override def removeAll(other: RangeSet[C, O]): Unit = other match {
    case that: RangeSetWrapperLike[C, O, Repr] => delegate.removeAll(that.delegate)
    case _                                     => super.removeAll(other)
  }
}
