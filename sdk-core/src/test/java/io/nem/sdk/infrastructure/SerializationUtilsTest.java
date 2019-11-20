/*
 * Copyright 2019 NEM
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

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link SerializationUtils}
 */

public class SerializationUtilsTest {

    @Test
    void toUnresolvedAddressFromNamespaceId() {

        Assertions.assertEquals("9168e0ae3a0168edbd00000000000000000000000000000000",
            Hex.toHexString(
                SerializationUtils
                    .fromUnresolvedAddressToByteBuffer(
                        NamespaceId.createFromName("this.currency", NetworkType.MIJIN_TEST),
                        NetworkType.MIJIN_TEST)
                    .array()));

        Assertions.assertEquals("69bd12bab1fe3689ab00000000000000000000000000000000",
            Hex.toHexString(
                SerializationUtils
                    .fromUnresolvedAddressToByteBuffer(
                        NamespaceId.createFromName("this.currency", NetworkType.MAIN_NET),
                        NetworkType.MAIN_NET)
                    .array()));

        Assertions.assertEquals("6168e0ae3a0168edbd00000000000000000000000000000000",
            Hex.toHexString(
                SerializationUtils
                    .fromUnresolvedAddressToByteBuffer(
                        NamespaceId.createFromName("this.currency", NetworkType.MIJIN),
                        NetworkType.MIJIN)
                    .array()));

        Assertions.assertEquals("99bd12bab1fe3689ab00000000000000000000000000000000",
            Hex.toHexString(
                SerializationUtils
                    .fromUnresolvedAddressToByteBuffer(
                        NamespaceId.createFromName("this.currency", NetworkType.TEST_NET),
                        NetworkType.TEST_NET)
                    .array()));
    }

}
