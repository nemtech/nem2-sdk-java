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

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link RentalFees} */
class RentalFeesTest {

  @Test
  void createRentalFees() {

    RentalFees info =
        new RentalFees(BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3));

    Assertions.assertNotNull(info);

    Assertions.assertEquals(1, info.getEffectiveRootNamespaceRentalFeePerBlock().intValue());
    Assertions.assertEquals(2, info.getEffectiveChildNamespaceRentalFee().intValue());
    Assertions.assertEquals(3, info.getEffectiveMosaicRentalFee().intValue());
  }
}
