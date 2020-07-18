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

package io.nem.symbol.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MosaicResolutionStatementTest {

    private final BigInteger height = BigInteger.valueOf(1473L);
    private final BigInteger height2 = BigInteger.valueOf(1500L);
    private final NetworkType networkType = NetworkType.MIJIN_TEST;

    private List<MosaicResolutionStatement> mosaicResolutionStatements;

    private final MosaicId mosaicId1 = new MosaicId("AAAAAAAAAAAAAAA1");
    private final MosaicId mosaicId2 = new MosaicId("AAAAAAAAAAAAAAA2");
    private final MosaicId mosaicId3 = new MosaicId("AAAAAAAAAAAAAAA3");
    private final MosaicId mosaicId4 = new MosaicId("AAAAAAAAAAAAAAA4");
    private final NamespaceId mosaicNamespace1 = NamespaceId.createFromName("mosaicnamespace1");
    private final NamespaceId mosaicNamespace3 = NamespaceId.createFromName("mosaicnamespace3");
    private final NamespaceId mosaicNamespace4 = NamespaceId.createFromName("mosaicnamespace4");
    private final NamespaceId addressNamespace1 = NamespaceId.createFromName("addressnamespace1");


    @BeforeEach
    void setupStatement() {

        MosaicResolutionStatement mosaicResolutionStatement1 = new MosaicResolutionStatement("abc", height,
            mosaicNamespace1, Arrays
            .asList(new ResolutionEntry<>(mosaicId1, new ReceiptSource(1, 0), ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId2, new ReceiptSource(3, 5), ReceiptType.MOSAIC_ALIAS_RESOLUTION)));

        MosaicResolutionStatement mosaicResolutionStatement2 = new MosaicResolutionStatement("abc", height,
            mosaicNamespace3, Collections.singletonList(
            new ResolutionEntry<>(mosaicId3, new ReceiptSource(3, 1), ReceiptType.MOSAIC_ALIAS_RESOLUTION)));

        MosaicResolutionStatement mosaicResolutionStatement3 = new MosaicResolutionStatement("abc", height2,
            mosaicNamespace4, Arrays
            .asList(new ResolutionEntry<>(mosaicId1, new ReceiptSource(1, 1), ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId2, new ReceiptSource(1, 4), ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId3, new ReceiptSource(1, 7), ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId4, new ReceiptSource(2, 4), ReceiptType.MOSAIC_ALIAS_RESOLUTION)));

        mosaicResolutionStatements = Arrays
            .asList(mosaicResolutionStatement1, mosaicResolutionStatement2, mosaicResolutionStatement3);


    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsGreaterThanMaxMosaicId() {
        Optional<MosaicId> resolved = MosaicResolutionStatement
            .getResolvedMosaicId(mosaicResolutionStatements, height, mosaicNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }


    @Test
    void shouldNotResolveMosaicIdWhenInvalidHeight() {
        Optional<MosaicId> resolved = MosaicResolutionStatement
            .getResolvedMosaicId(mosaicResolutionStatements, height2, addressNamespace1, 4, 0);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldGetResolvedEntryWhenRealMosaicId() {
        Optional<MosaicId> resolved = MosaicResolutionStatement
            .getResolvedMosaicId(mosaicResolutionStatements, height, mosaicNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsInMiddleOf2PirmaryIds() {
        Optional<MosaicId> resolved = MosaicResolutionStatement
            .getResolvedMosaicId(mosaicResolutionStatements, height, mosaicNamespace1, 2, 1);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId1, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdMatchesButNotSecondaryId() {
        Optional<MosaicId> resolved = MosaicResolutionStatement
            .getResolvedMosaicId(mosaicResolutionStatements, height, mosaicNamespace1, 3, 6);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }

    @Test
    void resolutionChangeInTheBlockMoreThanOneAggregate() {
        assertEquals(Optional.of(mosaicId1),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 1));

        assertEquals(Optional.of(mosaicId2),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 4));

        assertEquals(Optional.of(mosaicId3),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 7));

        assertEquals(Optional.of(mosaicId3),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 2, 1));

        assertEquals(Optional.of(mosaicId4),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 2, 4));

        assertEquals(Optional.empty(),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 0));

        assertEquals(Optional.of(mosaicId2),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 6));

        assertEquals(Optional.of(mosaicId1),
            MosaicResolutionStatement.getResolvedMosaicId(mosaicResolutionStatements, height2, mosaicNamespace4, 1, 2));


    }
}
