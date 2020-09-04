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

package io.nem.symbol.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SecretHashAlgorithmTest {

    @ParameterizedTest
    @EnumSource(value = SecretHashAlgorithm.class)
    void shouldBeExactly64CharactersLength(SecretHashAlgorithm type) {
        byte[] secretBytes = RandomUtils.generateRandomBytes(20);
        byte[] result = type.hash(secretBytes);
        String secret = ConvertUtils.toHex(result);
        assertTrue(SecretHashAlgorithm.validator(type, secret));
    }

    @ParameterizedTest
    @EnumSource(value = SecretHashAlgorithm.class)
    void shouldReturnFalseIfItIsNotAValidHash(SecretHashAlgorithm type) {
        byte[] secretBytes = RandomUtils.generateRandomBytes(20);
        byte[] result = type.hash(secretBytes);
        String secret = ConvertUtils.toHex(result) + "aaa";
        assertFalse(SecretHashAlgorithm.validator(type, secret));
    }

    @Test
    void HASH_160ShouldReturnTrueIfItIsNot64Or40CharsLength() {
        assertFalse(SecretHashAlgorithm
            .validator(SecretHashAlgorithm.HASH_160, "400C2CDA984F04D00C78417D2AED8443AF451E40D77721AC1F5E5334555F0"));
    }

    @Test
    void HASH_160ShouldReturnFalseIfItIsNotAValidHash() {
        String secret = "zyz6053bb910a6027f138ac5ebe92d43a9a18b7239b3c4d5ea69f1632e50aeef";
        assertFalse(SecretHashAlgorithm.validator(SecretHashAlgorithm.HASH_160, secret));
    }

}
