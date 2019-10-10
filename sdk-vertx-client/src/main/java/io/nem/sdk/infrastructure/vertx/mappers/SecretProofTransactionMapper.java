/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure.vertx.mappers;

import static io.nem.core.utils.MapperUtils.toAddressFromUnresolved;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.LockHashAlgorithmEnum;
import io.nem.sdk.openapi.vertx.model.SecretProofTransactionDTO;

/**
 * Secret proof transaction mapper.
 */
class SecretProofTransactionMapper extends
    AbstractTransactionMapper<SecretProofTransactionDTO, SecretProofTransaction> {

    public SecretProofTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.SECRET_PROOF, SecretProofTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<SecretProofTransaction> createFactory(NetworkType networkType,
        SecretProofTransactionDTO transaction) {
        return SecretProofTransactionFactory.create(
            networkType,
            LockHashAlgorithmType.rawValueOf(transaction.getHashAlgorithm().getValue()),
            toAddressFromUnresolved(transaction.getRecipientAddress()),
            transaction.getSecret(),
            transaction.getProof());
    }

    @Override
    protected void copyToDto(SecretProofTransaction transaction, SecretProofTransactionDTO dto) {
        dto.setHashAlgorithm(LockHashAlgorithmEnum.fromValue(transaction.getHashType().getValue()));
        dto.setRecipientAddress(transaction.getRecipient().encoded());
        dto.setSecret(transaction.getSecret());
        dto.setProof(transaction.getProof());
    }

}
