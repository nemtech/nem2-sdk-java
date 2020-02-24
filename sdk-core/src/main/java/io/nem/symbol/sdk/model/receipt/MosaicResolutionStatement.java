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

/**
 * {@link ResolutionStatement} specific for Mosaic Ids.
 */
public class MosaicResolutionStatement extends ResolutionStatement<UnresolvedMosaicId, MosaicId> {

    /**
     * Constructor
     *
     * @param height Height
     * @param unresolved An {@link UnresolvedMosaicId}
     * @param resolutionEntries Array of {@link MosaicId} resolution entries.
     */
    public MosaicResolutionStatement(BigInteger height, UnresolvedMosaicId unresolved,
        List<ResolutionEntry<MosaicId>> resolutionEntries) {
        super(ResolutionType.MOSAIC, height, unresolved, resolutionEntries);
    }
}
