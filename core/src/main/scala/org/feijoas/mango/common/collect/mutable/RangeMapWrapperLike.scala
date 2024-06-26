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

/** Implementation trait for mutable [[RangeMap]] that delegates to Guava
 *
 *  @author Markus Schneider
 *  @since 0.9
 */
@Beta
private[mango] trait RangeMapWrapperLike[K, V, O <: Ordering[K], +Repr <: RangeMapWrapperLike[K,
                                                                                              V,
                                                                                              O,
                                                                                              Repr
] with RangeMap[K, V, O]]
    extends collect.RangeMapWrapperLike[K, V, O, Repr]
    with RangeMap[K, V, O] {
  self =>

  override def clear(): Unit = delegate.clear()
  override def remove(range: Range[K, O]): Unit = delegate.remove(range.asJava)
  override def put(range: Range[K, O], value: V): Unit = delegate.put(range.asJava, value)

  override def putAll(rangeMap: collect.RangeMap[K, V, O]): Unit = rangeMap match {
    case that: RangeMapWrapperLike[K, V, O, Repr] => delegate.putAll(that.delegate)
    case _                                        => super.putAll(rangeMap)
  }
}
