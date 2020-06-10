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

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.BalanceChangeReceiptBuilder;
import io.nem.symbol.catapult.builders.MosaicBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.ReceiptTypeDto;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Optional;

public class BalanceChangeReceipt extends Receipt {

    private final Address targetAddress;
    private final MosaicId mosaicId;
    private final BigInteger amount;

    /**
     * Constructor BalanceChangeReceipt
     *
     * @param targetAddress Public Account
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     * @param size Receipt Size
     */
    public BalanceChangeReceipt(
        Address targetAddress,
        MosaicId mosaicId,
        BigInteger amount,
        ReceiptType type,
        ReceiptVersion version,
        Optional<Integer> size) {
        super(type, version, size);
        this.targetAddress = targetAddress;
        this.amount = amount;
        this.mosaicId = mosaicId;
        this.validateReceiptType(type);
    }

    /**
     * Constructor BalanceChangeReceipt
     *
     * @param targetAddress Public Account
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     */
    public BalanceChangeReceipt(
        Address targetAddress,
        MosaicId mosaicId,
        BigInteger amount,
        ReceiptType type,
        ReceiptVersion version) {
        this(targetAddress,
            mosaicId,
            amount,
            type,
            version, Optional.empty());
    }

    /**
     * Returns account
     *
     * @return account
     */
    public Address getTargetAddress() {
        return this.targetAddress;
    }

    /**
     * Returns mosaicId
     *
     * @return account
     */
    public MosaicId getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Returns balance change amount
     *
     * @return balance change amount
     */
    public BigInteger getAmount() {
        return this.amount;
    }

    /**
     * Serialize receipt and returns receipt bytes
     *
     * @return receipt bytes
     */
    @Override
    public byte[] serialize() {

        short version = (short) getVersion().getValue();
        ReceiptTypeDto type = ReceiptTypeDto.rawValueOf((short) getType().getValue());
        MosaicBuilder mosaic = MosaicBuilder
            .create(new MosaicIdDto(getMosaicId().getIdAsLong()),
                new AmountDto(getAmount().longValue()));
        AddressDto targetAddress = SerializationUtils.toAddressDto(getTargetAddress());
        return BalanceChangeReceiptBuilder
            .create(version, type, mosaic, targetAddress).serialize();
    }

    /**
     * Validate receipt type
     *
     * @return void
     */
    private void validateReceiptType(ReceiptType type) {
        if (!ReceiptType.BALANCE_CHANGE.contains(type)) {
            throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
        }
    }
}
