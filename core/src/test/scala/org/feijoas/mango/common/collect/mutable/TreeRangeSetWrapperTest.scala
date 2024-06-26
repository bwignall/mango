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

import com.google.common.collect.{RangeSet => GuavaRangeSet}
import org.feijoas.mango.common.collect.AsOrdered
import org.feijoas.mango.common.collect.RangeSetBehaviors
import org.feijoas.mango.common.collect.RangeSetWrapperBehaviours
import org.scalatest.freespec.AnyFreeSpec

import scala.math.Ordering.Int

/** Tests for [[ImmutableRangeSetWrapper]]
 *
 *  @author Markus Schneider
 *  @since 0.8
 */
class TreeRangeSetWrapperTest extends AnyFreeSpec with RangeSetBehaviors with RangeSetWrapperBehaviours {

  "A TreeRangeSetWrapper" - {
    behave.like(rangeSet(TreeRangeSetWrapper.newBuilder[Int, Int.type]))
    behave.like(mutableRangeSet(TreeRangeSetWrapper.newBuilder[Int, Int.type]))
    behave.like(rangeSetWithBuilder(TreeRangeSetWrapper.newBuilder[Int, Int.type]))
    behave.like(mutableWrapper((guava: GuavaRangeSet[AsOrdered[Int]]) => TreeRangeSetWrapper[Int, Int.type](guava)))
  }
}
