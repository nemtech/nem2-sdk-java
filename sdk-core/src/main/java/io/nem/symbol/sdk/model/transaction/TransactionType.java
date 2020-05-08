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

package io.nem.symbol.sdk.model.transaction;

import java.util.Arrays;

/**
 * Enum containing transaction type constants and the current versions for new transactions.
 */
public enum TransactionType {

    /**
     * Mosaic definition transaction type.
     */
    MOSAIC_DEFINITION(16717, 1),

    /**
     * Node key link transaction type.
     */
    NODE_KEY_LINK(16972, 1),

    /**
     * Mosaic supply change transaction.
     */
    MOSAIC_SUPPLY_CHANGE(16973, 1),

    /**
     * Register namespace transaction type.
     */
    NAMESPACE_REGISTRATION(16718, 1),

    /**
     * Address alias transaction type.
     */
    ADDRESS_ALIAS(16974, 1),

    /**
     * Mosaic alias transaction type.
     */
    MOSAIC_ALIAS(17230, 1),

    /**
     * Transfer Transaction transaction type.
     */
    TRANSFER(16724, 1),

    /**
     * Multisig account  modification transaction type.
     */
    MULTISIG_ACCOUNT_MODIFICATION(16725, 1),

    /**
     * Aggregate complete transaction type.
     */
    AGGREGATE_COMPLETE(16705, 1),

    /**
     * Aggregate bonded transaction type
     */
    AGGREGATE_BONDED(16961, 1),

    /**
     * Voting key link transaction builder.
     */
    VOTING_KEY_LINK(16707, 1),

    /**
     * Vrf key link transaction builder.
     */
    VRF_KEY_LINK(16963, 1),

    /**
     * Hash Lock transaction type
     */
    HASH_LOCK(16712, 1),

    /**
     * Account properties address transaction type
     */
    ACCOUNT_ADDRESS_RESTRICTION(16720, 1),

    /**
     * Account properties mosaic transaction type
     */
    ACCOUNT_MOSAIC_RESTRICTION(16976, 1),

    /**
     * Account properties entity type transaction type
     */
    ACCOUNT_OPERATION_RESTRICTION(17232, 1),

    /**
     * Secret Lock Transaction type
     */
    SECRET_LOCK(16722, 1),

    /**
     * Secret Proof transaction type
     */
    SECRET_PROOF(16978, 1),

    /**
     * Account metadata transaction type
     */
    ACCOUNT_METADATA(16708, 1),

    /**
     * Mosaic metadata transaction type
     */
    MOSAIC_METADATA(16964, 1),

    /**
     * Namespace metadata transaction type
     */
    NAMESPACE_METADATA(17220, 1),

    /**
     * Account link transaction type
     */
    ACCOUNT_LINK(16716, 1),

    /**
     * Mosaic address restriction type
     */
    MOSAIC_ADDRESS_RESTRICTION((short) 16977, 1),

    /**
     * Mosaic global restriction type
     */
    MOSAIC_GLOBAL_RESTRICTION((short) 16721, 1);

    /**
     * The transaction type value
     */
    private final int value;

    /**
     * Transaction format versions are defined in catapult-server in each transaction's plugin source code.
     *
     * <p>In [catapult-server](https://github.com/nemtech/catapult-server), the
     * `DEFINE_TRANSACTION_CONSTANTS` macro is used to define the `TYPE` and `VERSION` of the transaction format.
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
     * @param value the low level int value.
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
