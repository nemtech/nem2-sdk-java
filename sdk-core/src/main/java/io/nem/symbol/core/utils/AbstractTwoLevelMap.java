/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.symbol.core.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A two-level map of items. <br> Items are automatically created on access. Item associations are
 * order-dependent.
 */
public abstract class AbstractTwoLevelMap<K, V> {

    private final Map<K, Map<K, V>> impl = new ConcurrentHashMap<>();

    /**
     * Gets the TValue associated with key1 and key2.
     *
     * @param key1 The first key.
     * @param key2 The second key.
     * @return The value associated with key and key2.
     */
    public V getItem(final K key1, final K key2) {
        final Map<K, V> keyOneValues = this.getItems(key1);

        return keyOneValues.computeIfAbsent(key2, k -> createValue());
    }

    /**
     * Gets the (TKey, TValue) map associated with key.
     *
     * @param key The first key.
     * @return The map associated with key.
     */
    public Map<K, V> getItems(final K key) {
        return this.impl.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
    }

    /**
     * Removes a key from the map.
     *
     * @param key The key to remove.
     */
    public void remove(final K key) {
        this.impl.remove(key);
    }

    /**
     * Gets the key set of this map.
     *
     * @return The key set.
     */
    public Set<K> keySet() {
        return this.impl.keySet();
    }

    /**
     * Creates a new blank value.
     *
     * @return A new value.
     */
    protected abstract V createValue();
}
