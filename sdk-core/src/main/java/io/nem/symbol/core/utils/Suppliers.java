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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by fernando on 02/08/19.
 *
 * @author Fernando Boucquez
 */
public class Suppliers {

    /**
     * Private constructor for this utility class.
     */
    private Suppliers() {

    }

    /**
     * It generates a cached version of the supplier. The delegate supplier is only called once
     * regardless of how may the client calls get().
     *
     * @param delegate the delegate
     * @param <T> the type of the supplier response.
     * @return a cached version of the supplier.
     */
    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        AtomicReference<T> value = new AtomicReference<>();
        return () -> {
            T val = value.get();
            if (val == null) {
                val = value.updateAndGet(cur -> cur == null ?
                    Objects.requireNonNull(delegate.get()) : cur);
            }
            return val;
        };
    }
}
