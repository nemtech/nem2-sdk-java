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

import io.nem.sdk.model.account.PublicAccount;

/**
 * The multisig cosignatory modifications are part of the NEM's multisig account system. With a
 * multisig cosignatory modification a cosignatory is added to or deleted from a multisig account.
 * Multisig cosignatory modifications are part of a modify multisig account transactions.
 *
 * @since 1.0
 */
public class MultisigCosignatoryModification {

    private final CosignatoryModificationActionType modificationAction;
    private final PublicAccount cosignatoryPublicAccount;

    public MultisigCosignatoryModification(
        CosignatoryModificationActionType modificationAction, PublicAccount cosignatoryPublicAccount) {
        this.modificationAction = modificationAction;
        this.cosignatoryPublicAccount = cosignatoryPublicAccount;
    }

    public static MultisigCosignatoryModification create(CosignatoryModificationActionType modificationAction,
        PublicAccount cosignatoryPublicAccount) {
        return new MultisigCosignatoryModification(modificationAction, cosignatoryPublicAccount);
    }

    /**
     * Returns multisig modification type.
     *
     * @return {@link CosignatoryModificationActionType}
     */
    public CosignatoryModificationActionType getModificationAction() {
        return modificationAction;
    }

    /**
     * Returns cosignatory public account.
     *
     * @return {@link PublicAccount}
     */
    public PublicAccount getCosignatoryPublicAccount() {
        return cosignatoryPublicAccount;
    }
}
