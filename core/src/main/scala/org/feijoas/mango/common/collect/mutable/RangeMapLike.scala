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

import scala.collection.mutable.Growable
import scala.collection.mutable.Shrinkable

/** Implementation trait for mutable [[RangeMap]]
 *
 *  $rangeMapNote
 *  @author Markus Schneider
 *  @since 0.9
 */
@Beta
trait RangeMapLike[K, V, O <: Ordering[K], +Repr <: RangeMapLike[K, V, O, Repr] with RangeMap[K, V, O]]
    extends collect.RangeMapLike[K, V, O, Repr]
    with Growable[(Range[K, O], V)]
    with Shrinkable[Range[K, O]] {

  /** Maps a range to a specified value.
   *
   *  <p>Specifically, after a call to `put(range, value)`, if
   *  `range.contains(k)`, then `get(k)`
   *  will return `value`.
   *
   *  <p>If `range` is empty, then this is a no-op.
   */
  def put(range: Range[K, O], value: V): Unit

  /** Puts all the associations from `rangeMap` into this range map.
   */
  def putAll(rangeMap: collect.RangeMap[K, V, O]): Unit = rangeMap.asMapOfRanges().foreach { case (range, value) =>
    put(range, value)
  }

  /** Removes all associations from this range map.
   */
  def clear(): Unit

  /** Removes all associations from this range map in the specified range.
   *
   *  <p>If {@code !range.contains(k)}, `get(k)` will return the same result
   *  before and after a call to {@code remove(range)}.  If {@code range.contains(k)}, then
   *  after a call to {@code remove(range)}, {@code get(k)} will return {@code None}.
   */
  def remove(range: Range[K, O]): Unit

  /** Returns a view of the part of this range map that intersects with `range`.
   *
   *  <p>For example, if `rangeMap` had the entries
   *  {@code [1, 5] => "foo", (6, 8) => "bar", (10, \u2025) => "baz"}
   *  then `rangeMap.subRangeMap(Range.open(3, 12))` would return a range map
   *  with the entries {@code (3, 5) => "foo", (6, 8) => "bar", (10, 12) => "baz"}.
   *
   *  <p>The returned range map supports all optional operations that this range map supports.
   *
   *  <p>The returned range map will throw an `IllegalArgumentException` on an attempt to
   *  insert a range not enclosed by {@code range}.
   */
  @throws(classOf[IllegalArgumentException])
  override def subRangeMap(range: Range[K, O]): Repr

  /** Alias for `#put(range, value)` */
  final override def addOne(kv: (Range[K, O], V)): this.type = {
    put(kv._1, kv._2)
    this
  }

  /** Alias for `#remove(range)` */
  final override def subtractOne(range: Range[K, O]): this.type = {
    remove(range)
    this
  }
}
