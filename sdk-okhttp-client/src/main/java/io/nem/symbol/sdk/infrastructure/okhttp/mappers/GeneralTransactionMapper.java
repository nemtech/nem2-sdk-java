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
package io.nem.symbol.sdk.infrastructure.okhttp.mappers;

import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Entry point for the transaction mapping. This mapper should support all the known transactions.
 *
 * <p>It's basically a delegator to the specific {@link TransactionMapper} registered in this
 * object.
 */
public class GeneralTransactionMapper implements TransactionMapper {

  private final JsonHelper jsonHelper;

  private final Map<Pair<TransactionType, Integer>, TransactionMapper> transactionMappers =
      new HashMap<>();

  public GeneralTransactionMapper(JsonHelper jsonHelper) {
    this.jsonHelper = jsonHelper;
    Validate.notNull(jsonHelper, "jsonHelper must not be null");
    register(new AddressAliasTransactionMapper(jsonHelper));
    register(new HashLockTransactionMapper(jsonHelper));
    register(new MosaicAddressRestrictionTransactionMapper(jsonHelper));
    register(new MosaicAliasTransactionMapper(jsonHelper));
    register(new MosaicDefinitionTransactionMapper(jsonHelper));
    register(new MosaicGlobalRestrictionTransactionMapper(jsonHelper));
    register(new MosaicSupplyChangeTransactionMapper(jsonHelper));
    register(new MultisigAccountModificationTransactionMapper(jsonHelper));
    register(new NamespaceRegistrationTransactionMapper(jsonHelper));
    register(new SecretLockTransactionMapper(jsonHelper));
    register(new SecretProofTransactionMapper(jsonHelper));
    register(new TransferTransactionMapper(jsonHelper));
    register(new AccountMetadataTransactionMapper(jsonHelper));
    register(new MosaicMetadataTransactionMapper(jsonHelper));
    register(new NamespaceMetadataTransactionMapper(jsonHelper));
    register(new AccountAddressRestrictionTransactionMapper(jsonHelper));
    register(new AccountMosaicRestrictionTransactionMapper(jsonHelper));
    register(new AccountOperationRestrictionTransactionMapper(jsonHelper));
    register(new VrfKeyLinkTransactionMapper(jsonHelper));
    register(new NodeKeyLinkTransactionMapper(jsonHelper));
    register(new VotingKeyLinkTransactionMapper(jsonHelper));
    register(new VotingKeyLinkV1TransactionMapper(jsonHelper));
    register(new AccountKeyLinkTransactionMapper(jsonHelper));
    register(new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_BONDED, this));
    register(new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_COMPLETE, this));
  }

  private void register(TransactionMapper mapper) {
    if (transactionMappers.put(Pair.of(mapper.getTransactionType(), mapper.getVersion()), mapper)
        != null) {
      throw new IllegalArgumentException(
          "TransactionMapper for type "
              + mapper.getTransactionType()
              + " version "
              + mapper.getVersion()
              + " was already registered!");
    }
  }

  @Override
  public TransactionFactory<?> mapToFactoryFromDto(Object transactionInfoDTO) {
    try {
      Validate.notNull(transactionInfoDTO, "transactionInfoDTO must not be null");
      return resolveMapper(transactionInfoDTO).mapToFactoryFromDto(transactionInfoDTO);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Unknown error mapping transaction: "
              + ExceptionUtils.getMessage(e)
              + "\n"
              + jsonHelper.prettyPrint(transactionInfoDTO),
          e);
    }
  }

  @Override
  public Transaction mapFromDto(Object transactionInfoDTO) {
    return this.mapToFactoryFromDto(transactionInfoDTO).build();
  }

  @Override
  public Object mapToDto(Transaction transaction, Boolean embedded) {
    Validate.notNull(transaction, "transaction must not be null");
    return resolveMapper(transaction.getType(), transaction.getVersion())
        .mapToDto(transaction, embedded);
  }

  @Override
  public TransactionType getTransactionType() {
    // All transaction types supported.
    return null;
  }

  private TransactionMapper resolveMapper(Object transactionInfoJson) {
    Integer type = getJsonHelper().getInteger(transactionInfoJson, "transaction", "type");
    if (type == null) {
      throw new IllegalArgumentException(
          "Transaction cannot be mapped, object does not not have transaction type.");
    }
    Integer version = getJsonHelper().getInteger(transactionInfoJson, "transaction", "version");
    if (version == null) {
      throw new IllegalArgumentException(
          "Transaction cannot be mapped, object does not not have transaction version.");
    }
    TransactionType transactionType = TransactionType.rawValueOf(type);
    return resolveMapper(transactionType, version);
  }

  private TransactionMapper resolveMapper(TransactionType transactionType, int version) {
    TransactionMapper mapper = transactionMappers.get(Pair.of(transactionType, version));
    if (mapper == null) {
      throw new UnsupportedOperationException(
          "Unimplemented Transaction type " + transactionType + " version " + version);
    }
    return mapper;
  }

  public JsonHelper getJsonHelper() {
    return jsonHelper;
  }
}
