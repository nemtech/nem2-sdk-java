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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionInfo;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.EmbeddedTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.EmbeddedTransactionMetaDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NetworkTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionMetaDTO;

/**
 * Abstract transaction mapper for the transaction mappers that support a specific type of transaction (Account Link,
 * Mosaic Alias, etc.).
 *
 * @param <T> the dto type of the transaction object.
 */
public abstract class AbstractTransactionMapper<D, T extends Transaction> implements TransactionMapper {

    private final TransactionType transactionType;

    private final JsonHelper jsonHelper;

    private final Class<D> transactionDtoClass;

    public AbstractTransactionMapper(JsonHelper jsonHelper, TransactionType transactionType,
        Class<D> transactionDtoClass) {
        this.jsonHelper = jsonHelper;
        this.transactionType = transactionType;
        this.transactionDtoClass = transactionDtoClass;
    }

    @Override
    public Transaction mapFromDto(Object object) {
        return mapToFactoryFromDto(object).build();
    }

    @Override
    public TransactionFactory<T> mapToFactoryFromDto(Object object) {
        if (object instanceof EmbeddedTransactionInfoDTO) {
            EmbeddedTransactionInfoDTO transactionInfoDTO = (EmbeddedTransactionInfoDTO) object;
            TransactionInfo transactionInfo = createTransactionInfo(transactionInfoDTO.getMeta(),
                transactionInfoDTO.getId());
            return createFactory(transactionInfo, transactionInfoDTO.getTransaction());
        }
        TransactionInfoDTO transactionInfoDTO = this.jsonHelper.convert(object, TransactionInfoDTO.class);
        TransactionInfo transactionInfo = createTransactionInfo(transactionInfoDTO.getMeta(),
            transactionInfoDTO.getId());
        return createFactory(transactionInfo, transactionInfoDTO.getTransaction());
    }

    protected TransactionInfo createTransactionInfo(Object meta, String id) {
        if (meta == null) {
            return null;
        }
        if (this.jsonHelper.contains(meta, "aggregateHash")) {
            EmbeddedTransactionMetaDTO embedded = this.jsonHelper.convert(meta, EmbeddedTransactionMetaDTO.class);
            return TransactionInfo
                .createAggregate(embedded.getHeight(), embedded.getIndex(), id, embedded.getAggregateHash(),
                    embedded.getAggregateId());
        } else {
            TransactionMetaDTO toplevel = this.jsonHelper.convert(meta, TransactionMetaDTO.class);
            return TransactionInfo.create(toplevel.getHeight(), toplevel.getIndex(), id, toplevel.getHash(),
                toplevel.getMerkleComponentHash());
        }
    }


    @Override
    public Object mapToDto(Transaction transaction, Boolean embedded) {
        if (transaction.getTransactionInfo().flatMap(TransactionInfo::getAggregateHash).isPresent() || Boolean.TRUE
            .equals(embedded)) {
            EmbeddedTransactionInfoDTO dto = new EmbeddedTransactionInfoDTO();
            dto.setMeta(createTransactionInfoEmbedded(transaction));
            dto.setId(transaction.getRecordId().orElse(null));
            dto.setTransaction(mapTransaction(transaction, true));
            return dto;
        } else {
            TransactionInfoDTO dto = new TransactionInfoDTO();
            dto.setMeta(createTransactionInfo(transaction));
            dto.setId(transaction.getRecordId().orElse(null));
            dto.setTransaction(mapTransaction(transaction, false));
            return dto;
        }
    }

    protected final TransactionFactory<T> createFactory(TransactionInfo transactionInfo, Object transactionDto) {
        D transaction = getJsonHelper().convert(transactionDto, transactionDtoClass);
        TransactionDTO transactionDTO = getJsonHelper().convert(transactionDto, TransactionDTO.class);
        NetworkType networkType = NetworkType.rawValueOf(transactionDTO.getNetwork().getValue());
        TransactionFactory<T> factory = createFactory(networkType, transaction);
        factory.version(transactionDTO.getVersion());
        if (transactionDTO.getDeadline() != null) {
            factory.deadline(new Deadline(transactionDTO.getDeadline()));
        }
        if (transactionDTO.getSignerPublicKey() != null) {
            factory.signer(PublicAccount.createFromPublicKey(transactionDTO.getSignerPublicKey(), networkType));
        }
        if (transactionDTO.getSignature() != null) {
            factory.signature(transactionDTO.getSignature());
        }
        if (transactionDTO.getMaxFee() != null) {
            factory.maxFee(transactionDTO.getMaxFee());
        }
        if (transactionInfo != null) {
            factory.transactionInfo(transactionInfo);
        }
        if (factory.getType() != getTransactionType()) {
            throw new IllegalStateException(
                "Expected transaction to be " + getTransactionType() + " but got " + factory.getType());
        }
        return factory;
    }

    protected abstract TransactionFactory<T> createFactory(NetworkType networkType, D transaction);


    private EmbeddedTransactionMetaDTO createTransactionInfoEmbedded(Transaction transaction) {
        return transaction.getTransactionInfo().map(i -> {
            EmbeddedTransactionMetaDTO dto = new EmbeddedTransactionMetaDTO();
            dto.setHeight(i.getHeight());
            dto.setAggregateHash(i.getAggregateHash().orElse(null));
            dto.setIndex(i.getIndex().orElse(null));
            dto.setAggregateId(i.getAggregateId().orElse(null));
            return dto;
        }).orElse(null);
    }

    private TransactionMetaDTO createTransactionInfo(Transaction transaction) {
        return transaction.getTransactionInfo().map(i -> {
            TransactionMetaDTO dto = new TransactionMetaDTO();
            dto.setHeight(i.getHeight());
            dto.setHash(i.getHash().orElse(null));
            dto.setIndex(i.getIndex().orElse(null));
            dto.setMerkleComponentHash(i.getMerkleComponentHash().orElse(null));
            return dto;
        }).orElse(null);
    }

    private D mapTransaction(Transaction transaction, boolean embedded) {

        TransactionDTO dto = new TransactionDTO();
        dto.setSignerPublicKey(
            transaction.getSigner().map(PublicAccount::getPublicKey).map(PublicKey::toHex).orElse(null));

        dto.setVersion(transaction.getVersion());
        dto.setType(transaction.getType().getValue());
        dto.setNetwork(NetworkTypeEnum.fromValue(transaction.getNetworkType().getValue()));
        if (!embedded) {
            dto.setMaxFee(transaction.getMaxFee());
            dto.setDeadline(transaction.getDeadline().toBigInteger());
            dto.setSignature(transaction.getSignature().orElse(null));
        }

        D specificDto = getJsonHelper().parse(getJsonHelper().print(dto), transactionDtoClass);
        copyToDto((T) transaction, specificDto);
        return specificDto;
    }

    /**
     * Subclasses need to map the values from the transaction model to the transaction dto. Only the specific fields
     * need to be mapped, not the common like maxFee or deadline as they are done in this abstract class.
     *
     * @param transaction the transaction model
     * @param dto the transaction dto.
     */
    protected abstract void copyToDto(T transaction, D dto);


    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    @Override
    public TransactionType getTransactionType() {
        return transactionType;
    }

}
