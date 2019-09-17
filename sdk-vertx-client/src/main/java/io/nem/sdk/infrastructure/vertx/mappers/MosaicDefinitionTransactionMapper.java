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

import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.MosaicDefinitionTransactionDTO;

class MosaicDefinitionTransactionMapper extends
    AbstractTransactionMapper<MosaicDefinitionTransactionDTO> {

    public MosaicDefinitionTransactionMapper(JsonHelper jsonHelper, SignSchema signSchema) {
        super(jsonHelper, TransactionType.MOSAIC_DEFINITION, MosaicDefinitionTransactionDTO.class,
            signSchema
        );
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        MosaicDefinitionTransactionDTO transaction) {

        Deadline deadline = new Deadline(transaction.getDeadline());

        String flags = "00" + Integer.toBinaryString(transaction.getFlags().intValue());
        String bitMapFlags = flags.substring(flags.length() - 2);
        MosaicProperties properties =
            MosaicProperties.create(
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                transaction.getDivisibility(),
                transaction.getDuration());

        return new MosaicDefinitionTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            MosaicNonce
                .createFromBigInteger(transaction.getNonce()),
            toMosaicId(transaction.getId()),
            properties,
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSignerPublicKey(),
                extractNetworkType(transaction.getVersion()), getSignSchema()),
            transactionInfo);
    }
}
