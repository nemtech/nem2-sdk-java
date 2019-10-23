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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

public class KeyPairTest {

    // region basic construction

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorCanCreateNewKeyPair(SignSchema signSchema) {
        // Act:
        final KeyPair kp = KeyPair.random(signSchema);

        // Assert:
        MatcherAssert.assertThat(kp.hasPrivateKey(), IsEqual.equalTo(true));
        MatcherAssert.assertThat(kp.getPrivateKey(), IsNull.notNullValue());
        MatcherAssert.assertThat(kp.getPublicKey(), IsNull.notNullValue());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorCanCreateKeyPairAroundPrivateKey(SignSchema signSchema) {
        // Arrange:
        final KeyPair kp1 = KeyPair.random(signSchema);

        // Act:
        final KeyPair kp2 = KeyPair.fromPrivate(kp1.getPrivateKey(), signSchema);

        // Assert:
        MatcherAssert.assertThat(kp2.hasPrivateKey(), IsEqual.equalTo(true));
        MatcherAssert.assertThat(kp2.getPrivateKey(), IsEqual.equalTo(kp1.getPrivateKey()));
        MatcherAssert.assertThat(kp2.getPublicKey(), IsEqual.equalTo(kp1.getPublicKey()));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorCanCreateKeyPairAroundPublicKey(SignSchema signSchema) {
        // Arrange:
        final KeyPair kp1 = KeyPair.random(signSchema);

        // Act:
        final KeyPair kp2 = KeyPair.onlyPublic(kp1.getPublicKey());

        // Assert:
        MatcherAssert.assertThat(kp2.hasPrivateKey(), IsEqual.equalTo(false));
        MatcherAssert.assertThat(kp2.getPublicKey(), IsEqual.equalTo(kp1.getPublicKey()));

        IllegalStateException exception = Assertions
            .assertThrows(IllegalStateException.class, kp2::getPrivateKey);
        Assertions.assertEquals("Private Key hasn't been provided.", exception.getMessage());
    }

    // endregion

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorCreatesDifferentInstancesWithDifferentKeys(
        SignSchema signSchema) {
        // Act:
        final KeyPair kp1 = KeyPair.random(signSchema);
        final KeyPair kp2 = KeyPair.random(signSchema);

        // Assert:
        MatcherAssert.assertThat(kp2.getPrivateKey(), IsNot.not(IsEqual.equalTo(kp1.getPrivateKey())));
        MatcherAssert.assertThat(kp2.getPublicKey(), IsNot.not(IsEqual.equalTo(kp1.getPublicKey())));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorFailsIfPublicKeyIsNotCompressed(SignSchema signSchema) {
        // Arrange:
        final KeyPairContext context = new KeyPairContext(signSchema);
        final PublicKey publicKey = Mockito.mock(PublicKey.class);
        Mockito.when(context.analyzer.isKeyCompressed(publicKey)).thenReturn(false);

        // Act:
        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class,
                () -> KeyPair.onlyPublic(publicKey, context.engine));
        Assertions.assertEquals("PublicKey must be in compressed form", exception.getMessage());
    }

    // region delegation

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorCreatesKeyGenerator(SignSchema signSchema) {
        // Arrange:
        final KeyPairContext context = new KeyPairContext(signSchema);

        // Act:
        KeyPair.random(context.engine, signSchema);

        // Assert:
        Mockito.verify(context.engine, Mockito.times(1)).createKeyGenerator(signSchema);
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorDelegatesKeyGenerationToKeyGenerator(SignSchema signSchema) {
        // Arrange:
        final KeyPairContext context = new KeyPairContext(signSchema);

        // Act:
        KeyPair.random(context.engine, signSchema);

        // Assert:
        Mockito.verify(context.generator, Mockito.times(1)).generateKeyPair();
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void ctorWithPrivateKeyDelegatesToDerivePublicKey(SignSchema signSchema) {
        // Arrange:
        final KeyPairContext context = new KeyPairContext(signSchema);

        // Act:
        KeyPair.fromPrivate(context.privateKey, context.engine, signSchema);

        // Assert:
        Mockito.verify(context.generator, Mockito.times(1)).derivePublicKey(context.privateKey);
    }

    private class KeyPairContext {

        private final CryptoEngine engine = Mockito.mock(CryptoEngine.class);
        private final KeyAnalyzer analyzer = Mockito.mock(KeyAnalyzer.class);
        private final KeyGenerator generator = Mockito.mock(KeyGenerator.class);
        private final PrivateKey privateKey = Mockito.mock(PrivateKey.class);
        private final PublicKey publicKey = Mockito.mock(PublicKey.class);
        private final KeyPair keyPair1 = Mockito.mock(KeyPair.class);

        private KeyPairContext(SignSchema signSchema) {
            Mockito.when(this.analyzer.isKeyCompressed(Mockito.any())).thenReturn(true);
            Mockito.when(this.engine.createKeyAnalyzer()).thenReturn(this.analyzer);
            Mockito.when(this.engine.createKeyGenerator(signSchema)).thenReturn(this.generator);
            Mockito.when(this.generator.generateKeyPair()).thenReturn(this.keyPair1);
            Mockito.when(this.generator.derivePublicKey(this.privateKey))
                .thenReturn(this.publicKey);
        }
    }

    // endregion
}
