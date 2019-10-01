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

package io.nem.sdk.model.transaction;

import java.util.Arrays;

/**
 * Enum containing transaction type constants and the current versions for new transactions.
 */
public enum TransactionType {

    // Mosaic
    /**
     * Mosaic definition transaction type.
     */
    MOSAIC_DEFINITION(0x414D, 1),

    /**
     * Mosaic supply change transaction.
     */
    MOSAIC_SUPPLY_CHANGE(0x424D, 1),

    // Namespace
    /**
     * Register namespace transaction type.
     */
    REGISTER_NAMESPACE(0x414E, 1),

    /**
     * Address alias transaction type.
     */
    ADDRESS_ALIAS(0x424E, 1),

    /**
     * Mosaic alias transaction type.
     */
    MOSAIC_ALIAS(0x434E, 1),

    // Transfer
    /**
     * Transfer Transaction transaction type.
     */
    TRANSFER(0x4154, 1),

    // Multisignature
    /**
     * Modify multisig account transaction type.
     */
    MODIFY_MULTISIG_ACCOUNT(0x4155, 1),

    /**
     * Aggregate complete transaction type.
     */
    AGGREGATE_COMPLETE(0x4141, 1),

    /**
     * Aggregate bonded transaction type
     */
    AGGREGATE_BONDED(0x4241, 1),

    /**
     * Hash Lock transaction type
     */
    LOCK(0x4148, 1),

    // Account filters
    /**
     * Account properties address transaction type
     */
    ACCOUNT_ADDRESS_RESTRICTION(0x4150, 1),

    /**
     * Account properties mosaic transaction type
     */
    ACCOUNT_MOSAIC_RESTRICTION(0x4250, 1),

    /**
     * Account properties entity type transaction type
     */
    ACCOUNT_OPERATION_RESTRICTION(0x4350, 1),

    // Cross-chain swaps
    /**
     * Secret Lock Transaction type
     */
    SECRET_LOCK(0x4152, 1),

    /**
     * Secret Proof transaction type
     */
    SECRET_PROOF(0x4252, 1),
    /**
     * Account metadata transaction version
     */
    ACCOUNT_METADATA_TRANSACTION(0x4144, 1),
    /**
     * Mosaic metadata transaction version
     */
    MOSAIC_METADATA_TRANSACTION(0x4244, 1),
    /**
     * Namespace metadata transaction version
     */
    NAMESPACE_METADATA_TRANSACTION(0x4344, 1),
    /**
     * Account link transaction type
     */
    ACCOUNT_LINK(0x414C, 1),
    /**
     * Mosaic address restriction type
     */
    MOSAIC_ADDRESS_RESTRICTION((short) 0x4251, 1),
    /**
     * Mosaic global restriction type
     */
    MOSAIC_GLOBAL_RESTRICTION((short) 0x4151, 1);

    /**
     * The transaction type value
     */
    private final int value;

    /**
     * Transaction format versions are defined in catapult-server in each transaction's plugin
     * source code.
     *
     * <p>In [catapult-server](https://github.com/nemtech/catapult-server), the
     * `DEFINE_TRANSACTION_CONSTANTS` macro is used to define the `TYPE` and `VERSION` of the
     * transaction format.
     *
     * @see <a href="https://github.com/nemtech/catapult-server/blob/master/plugins/txes/transfer/src/model/TransferTransaction.h#L37"/>
     */
    private final int currentVersion;

    TransactionType(int value, int currentVersion) {
        this.value = value;
        this.currentVersion = currentVersion;
    }

    /**
     * Static constructor converting transaction type raw value to enum instance.
     *
     * @return {@link TransactionType}
     */
    public static TransactionType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the current version for new transactions.
     *
     * @return the default version.
     */
    public int getCurrentVersion() {
        return currentVersion;
    }
}
