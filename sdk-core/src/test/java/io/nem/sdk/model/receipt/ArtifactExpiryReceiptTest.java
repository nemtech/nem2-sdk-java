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

package io.nem.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArtifactExpiryReceiptTest {

    @Test
    void shouldCreateMosaicExpiryReceipt() {
        MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
        ArtifactExpiryReceipt<MosaicId> mosaicExpiryReceipt =
            new ArtifactExpiryReceipt<>(
                mosaicId, ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(ReceiptType.MOSAIC_EXPIRED, mosaicExpiryReceipt.getType());
        assertFalse(mosaicExpiryReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.ARTIFACT_EXPIRY, mosaicExpiryReceipt.getVersion());
        assertEquals("85BBEA6CC462B244",
            mosaicExpiryReceipt.getArtifactId().getIdAsHex().toUpperCase());

        String hex = ConvertUtils.toHex(mosaicExpiryReceipt.serialize());
        Assertions.assertEquals("01004D4144B262C46CEABB85", hex);
    }

    @Test
    void shouldCreateNamespaceExpiryReceipt() {
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("9562080086528621131"));
        ArtifactExpiryReceipt<NamespaceId> namespaceExpiryReceipt =
            new ArtifactExpiryReceipt<>(
                namespaceId, ReceiptType.NAMESPACE_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(ReceiptType.NAMESPACE_EXPIRED, namespaceExpiryReceipt.getType());
        assertFalse(namespaceExpiryReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.ARTIFACT_EXPIRY, namespaceExpiryReceipt.getVersion());
        assertEquals(
            namespaceExpiryReceipt.getArtifactId().getId(), new BigInteger("9562080086528621131"));

        String hex = ConvertUtils.toHex(namespaceExpiryReceipt.serialize());
        Assertions.assertEquals("01004E414BFA5F372D55B384", hex);
    }

    @Test
    void shouldCreateMosaicExpiryReceiptWithSize() {
        MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
        ArtifactExpiryReceipt<MosaicId> mosaicExpiryReceipt =
            new ArtifactExpiryReceipt<>(
                mosaicId, ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY,
                Optional.of(100));
        assertEquals(ReceiptType.MOSAIC_EXPIRED, mosaicExpiryReceipt.getType());
        assertEquals(ReceiptVersion.ARTIFACT_EXPIRY, mosaicExpiryReceipt.getVersion());
        assertEquals("85BBEA6CC462B244",
            mosaicExpiryReceipt.getArtifactId().getIdAsHex().toUpperCase());
        assertEquals(100, mosaicExpiryReceipt.getSize().get().intValue());

        String hex = ConvertUtils.toHex(mosaicExpiryReceipt.serialize());
        Assertions.assertEquals("01004D4144B262C46CEABB85", hex);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new ArtifactExpiryReceipt<>(
                    "", ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY, null);
            });
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
                new ArtifactExpiryReceipt<>(
                    mosaicId, ReceiptType.LOCK_HASH_COMPLETED, ReceiptVersion.ARTIFACT_EXPIRY,
                    null);
            });
    }
}
