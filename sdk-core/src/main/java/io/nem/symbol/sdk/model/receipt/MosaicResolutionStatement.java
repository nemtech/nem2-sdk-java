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

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link ResolutionStatement} specific for Mosaic Ids.
 */
public class MosaicResolutionStatement extends ResolutionStatement<UnresolvedMosaicId, MosaicId> {

    /**
     * Constructor
     *
     * @param recordId the database id if known.
     * @param height Height
     * @param unresolved An {@link UnresolvedMosaicId}
     * @param resolutionEntries Array of {@link MosaicId} resolution entries.
     */
    public MosaicResolutionStatement(String recordId, BigInteger height, UnresolvedMosaicId unresolved,
        List<ResolutionEntry<MosaicId>> resolutionEntries) {
        super(recordId, ResolutionType.MOSAIC, height, unresolved, resolutionEntries);
    }

    /**
     * This method tries to resolve the unresolved mosaic id using the the resolution entries.
     *
     * @param statements the statements.
     * @param height the height of the transaction.
     * @param mosaicAlias the {@link UnresolvedMosaicId}
     * @param primaryId the primary id
     * @param secondaryId the secondary id
     * @return the {@link Optional} of the resolved {@link MosaicId}
     */
    public static Optional<MosaicId> getResolvedMosaicId(List<MosaicResolutionStatement> statements, BigInteger height,
        UnresolvedMosaicId mosaicAlias, long primaryId, long secondaryId) {
        if (mosaicAlias instanceof MosaicId) {
            return Optional.of((MosaicId) mosaicAlias);
        }
        return statements.stream().filter(s -> height.equals(s.getHeight()))
            .filter(r -> r.getUnresolved().equals(mosaicAlias))
            .map(r -> r.getResolutionEntryById(primaryId, secondaryId).map(ResolutionEntry::getResolved)).findFirst()
            .flatMap(Function.identity());
    }
}
