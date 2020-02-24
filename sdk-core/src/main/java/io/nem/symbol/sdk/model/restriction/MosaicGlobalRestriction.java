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

package io.nem.symbol.sdk.model.restriction;

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Map;

/**
 * Mosaic global restriction structure describes restriction information for an mosaic.
 */
public class MosaicGlobalRestriction {

    /**
     * composite hash
     */
    public final String compositeHash;

    /**
     * Mosaic restriction entry type.
     */
    public final MosaicRestrictionEntryType entryType;
    /**
     * Mosaic identifier.
     */
    private final MosaicId mosaicId;

    /**
     * Mosaic restriction items
     */
    private final Map<BigInteger, MosaicGlobalRestrictionItem> restrictions;

    public MosaicGlobalRestriction(String compositeHash,
        MosaicRestrictionEntryType entryType,
        MosaicId mosaicId,
        Map<BigInteger, MosaicGlobalRestrictionItem> restrictions) {
        this.compositeHash = compositeHash;
        this.entryType = entryType;
        this.mosaicId = mosaicId;
        this.restrictions = restrictions;
    }

    public String getCompositeHash() {
        return compositeHash;
    }

    public MosaicRestrictionEntryType getEntryType() {
        return entryType;
    }

    public MosaicId getMosaicId() {
        return mosaicId;
    }


    public Map<BigInteger, MosaicGlobalRestrictionItem> getRestrictions() {
        return restrictions;
    }
}
