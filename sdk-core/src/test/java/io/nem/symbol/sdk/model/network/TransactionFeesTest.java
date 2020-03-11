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

package io.nem.symbol.sdk.model.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link TransactionFees}
 */
class TransactionFeesTest {


    @Test
    void createTransactionFees() {

        TransactionFees info = new TransactionFees(1, 2, 3, 4);

        Assertions.assertNotNull(info);

        Assertions.assertEquals(1, info.getAverageFeeMultiplier());
        Assertions.assertEquals(2, info.getMedianFeeMultiplier());
        Assertions.assertEquals(3, info.getLowestFeeMultiplier());
        Assertions.assertEquals(4, info.getHighestFeeMultiplier());

    }
}
