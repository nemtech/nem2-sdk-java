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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.math.BigInteger;
import java.util.List;

/**
 * {@link ResolutionStatement} specific for addresses.
 */
public class AddressResolutionStatement extends ResolutionStatement<UnresolvedAddress, Address> {

    /**
     * Constructor
     *
     * @param height Height
     * @param unresolved an {@link UnresolvedAddress}
     * @param resolutionEntries Array of {@link Address} resolution entries.
     */
    public AddressResolutionStatement(BigInteger height, UnresolvedAddress unresolved,
        List<ResolutionEntry<Address>> resolutionEntries) {
        super(ResolutionType.ADDRESS, height, unresolved, resolutionEntries);
    }
}
