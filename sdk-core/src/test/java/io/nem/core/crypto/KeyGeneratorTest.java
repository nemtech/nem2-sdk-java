/*
 * Copyright 2018 NEM
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

package io.nem.core.crypto;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public abstract class KeyGeneratorTest {

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void generateKeyPairReturnsNewKeyPair(SignSchema signSchema) {
        // Arrange:
        final KeyGenerator generator = this.getKeyGenerator(signSchema);

        // Act:
        final KeyPair kp = generator.generateKeyPair();

        // Assert:
        MatcherAssert.assertThat(kp.getPrivateKey(), IsNull.notNullValue());
        MatcherAssert.assertThat(kp.getPublicKey(), IsNull.notNullValue());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void derivePublicKeyReturnsPublicKey(SignSchema signSchema) {
        // Arrange:
        final KeyGenerator generator = this.getKeyGenerator(signSchema);
        final KeyPair kp = generator.generateKeyPair();

        // Act:
        final PublicKey publicKey = generator.derivePublicKey(kp.getPrivateKey());

        // Assert:
        MatcherAssert.assertThat(publicKey, IsNull.notNullValue());
        MatcherAssert.assertThat(publicKey.getBytes(), IsEqual.equalTo(kp.getPublicKey().getBytes()));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void generateKeyPairCreatesDifferentInstancesWithDifferentKeys(SignSchema signSchema) {
        // Act:
        final KeyPair kp1 = this.getKeyGenerator(signSchema).generateKeyPair();
        final KeyPair kp2 = this.getKeyGenerator(signSchema).generateKeyPair();

        // Assert:
        MatcherAssert.assertThat(kp2.getPrivateKey(), IsNot.not(IsEqual.equalTo(kp1.getPrivateKey())));
        MatcherAssert.assertThat(kp2.getPublicKey(), IsNot.not(IsEqual.equalTo(kp1.getPublicKey())));
    }

    protected KeyGenerator getKeyGenerator(SignSchema signSchema) {
        return this.getCryptoEngine().createKeyGenerator(signSchema);
    }

    protected abstract CryptoEngine getCryptoEngine();
}
