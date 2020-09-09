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
package io.nem.symbol.sdk.model.account;

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test ActivityBucket */
class ActivityBucketTest {

  @Test
  void constructor() {
    BigInteger startHeight = BigInteger.ONE;
    BigInteger totalFeesPaid = BigInteger.valueOf(2);
    int beneficiaryCount = 3;
    BigInteger rawScore = BigInteger.valueOf(4);
    ActivityBucket bucket =
        new ActivityBucket(startHeight, totalFeesPaid, beneficiaryCount, rawScore);

    Assertions.assertEquals(startHeight, bucket.getStartHeight());
    Assertions.assertEquals(totalFeesPaid, bucket.getTotalFeesPaid());
    Assertions.assertEquals(beneficiaryCount, bucket.getBeneficiaryCount());
    Assertions.assertEquals(rawScore, bucket.getRawScore());
  }
}
