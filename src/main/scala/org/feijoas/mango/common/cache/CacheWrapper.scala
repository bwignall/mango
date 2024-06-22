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
package org.feijoas.mango.common.cache

import com.google.common.cache.Cache as GuavaCache
import org.feijoas.mango.common.base.Functions.asCallableConverter
import org.feijoas.mango.common.cache.CacheStats.asMangoCacheStatsConverter

import scala.collection.{concurrent, immutable, mutable}
import scala.jdk.CollectionConverters.{ConcurrentMapHasAsScala, IterableHasAsJava, MapHasAsScala, MutableMapHasAsJava}

/** An adapter that wraps a Guava-Cache in a Mango-Cache and forwards all
 *  method calls to the underlying Guava-Cache.
 *
 *  @author Markus Schneider
 *  @since 0.7
 */
protected[mango] trait CacheWrapper[K, V] extends Cache[K, V] {

  /** Returns the backing Guava `Cache` delegate instance that methods are forwarded to.
   *  Concrete subclasses override this method to supply the instance being decorated.
   */
  protected def cache: GuavaCache[K, V]

  // forward all other methods to the Guava cache

  override def getIfPresent(key: K): Option[V] = Option(cache.getIfPresent(key))
  override def getOrElseUpdate(key: K, loader: () => V): V = cache.get(key, loader.asJava)
  override def getAllPresent(keys: Iterable[K]): immutable.Map[K, V] = {
    val guavaMap = cache.getAllPresent(keys.asJava)

    // TODO: The whole Guava map is copied to a new map. Since both maps are
    // immutable this can be replaced as soon as we have light-weight wrappers
    // around Guavas immutable collections
    immutable.Map.empty ++ guavaMap.asScala
  }

  override def put(key: K, value: V): Unit = cache.put(key, value)
  override def putAll(kvs: Iterable[(K, V)]): Unit = {
    val map = mutable.Map.empty ++ kvs
    cache.putAll(map.asJava)
  }
  override def invalidate(key: K): Unit = cache.invalidate(key)
  override def invalidateAll(keys: Iterable[K]): Unit = {
    cache.invalidateAll(keys.asJava)
  }
  override def invalidateAll(): Unit = cache.invalidateAll()
  override def size(): Long = cache.size()
  override def stats(): CacheStats = cache.stats().asScala
  override def cleanUp(): Unit = cache.cleanUp()
  override def asMap(): concurrent.Map[K, V] = cache.asMap().asScala
}

private[mango] object CacheWrapper {

  /** Factory method to create a `CacheWrapper[K, V]` from a Guava `Cache[K, V]`
   */
  def apply[K, V](guavaCache: GuavaCache[K, V]): CacheWrapper[K, V] = new CacheWrapper[K, V] {
    override def cache: GuavaCache[K, V] = guavaCache
  }
}
