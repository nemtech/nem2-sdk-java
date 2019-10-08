/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.restriction;

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;

/**
 * Mosaic global restriction item structure .
 */
public class MosaicGlobalRestrictionItem {


    /**
     * Reference mosaic identifier
     */
    private final MosaicId referenceMosaicId;
    /**
     * Mosaic restriction value.
     */
    private final BigInteger restrictionValue;

    /**
     * Mosaic restriction type.
     */
    private final MosaicRestrictionType restrictionType;

    public MosaicGlobalRestrictionItem(MosaicId referenceMosaicId,
        BigInteger restrictionValue, MosaicRestrictionType restrictionType) {
        this.referenceMosaicId = referenceMosaicId;
        this.restrictionValue = restrictionValue;
        this.restrictionType = restrictionType;
    }

    public MosaicId getReferenceMosaicId() {
        return referenceMosaicId;
    }

    public BigInteger getRestrictionValue() {
        return restrictionValue;
    }

    public MosaicRestrictionType getRestrictionType() {
        return restrictionType;
    }
}
