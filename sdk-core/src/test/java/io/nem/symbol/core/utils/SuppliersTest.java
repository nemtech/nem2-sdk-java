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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Suppliers}.
 *
 * @author Fernando Boucquez
 */
public class SuppliersTest {

    @Test
    public void memoize() {

        AtomicInteger calledTimes = new AtomicInteger();
        Supplier<String> delegate = () -> {
            calledTimes.incrementAndGet();
            return "The value";
        };
        Supplier<String> memoize = Suppliers.memoize(delegate);
        Assertions.assertEquals(0, calledTimes.get());
        memoize.get();
        Assertions.assertEquals(1, calledTimes.get());
        memoize.get();
        memoize.get();
        Assertions.assertEquals(1, calledTimes.get());
        memoize.get();
        memoize.get();
        memoize.get();
        Assertions.assertEquals(1, calledTimes.get());
    }
}
