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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.AggregateTransactionService;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of the {@link AggregateTransactionService}
 */
public class AggregateTransactionServiceImpl implements AggregateTransactionService {


    private final MultisigRepository multisigRepository;
    private final NetworkRepository networkRepository;

    public AggregateTransactionServiceImpl(RepositoryFactory repositoryFactory) {
        this.multisigRepository = repositoryFactory.createMultisigRepository();
        this.networkRepository = repositoryFactory.createNetworkRepository();
    }

    @Override
    public Observable<Boolean> isComplete(SignedTransaction signedTransaction) {

        Validate.notNull(signedTransaction, "signedTransaction is required");
        Validate.isTrue(signedTransaction.getType() == TransactionType.AGGREGATE_COMPLETE,
            "signedTransaction type must be AGGREGATE_COMPLETE");

        AggregateTransaction transaction = (AggregateTransaction) BinarySerializationImpl.INSTANCE
            .deserialize(
                ConvertUtils.fromHexToBytes(signedTransaction.getPayload()));

        /*
         * Include both initiator & cosigners
         */
        Set<Address> signers = transaction.getCosignatures().stream()
            .map(AggregateTransactionCosignature::getSigner)
            .map(PublicAccount::getAddress).collect(Collectors.toSet());

        signers.add(signedTransaction.getSigner().getAddress());

        return Observable.fromIterable(transaction.getInnerTransactions())
            .flatMap(innerTransaction ->
                multisigRepository.getMultisigAccountInfo(
                    innerTransaction.getSigner().orElseThrow(IllegalArgumentException::new)
                        .getAddress()).flatMap(multisigAccountInfo ->
                    multisigAccountInfo.getMinRemoval() != 0
                        && multisigAccountInfo.getMinApproval() != 0 ? multisigRepository
                        .getMultisigAccountGraphInfo(multisigAccountInfo.getAccountAddress())
                        .map(graphInfo -> validateCosignatories(graphInfo, signers,
                            innerTransaction)) : Observable.just(signers.stream()
                        .anyMatch(s -> s.equals(multisigAccountInfo.getAccountAddress()))))
            ).all(v -> v).toObservable();
    }

    @Override
    public Observable<Integer> getMaxCosignatures(Address address) {
        return this.multisigRepository.getMultisigAccountGraphInfo(address)
            .map(multisigAccountGraphInfo -> {
                Stream<Address> publicAccountStream = multisigAccountGraphInfo
                    .getMultisigEntries().values().stream().flatMap(
                        accounts -> accounts.stream()
                            .flatMap(account -> account.getCosignatoryAddresses().stream()));
                return publicAccountStream.collect(Collectors.toSet()).size();
            });
    }

    @Override
    public Observable<Integer> getNetworkMaxCosignaturesPerAggregate() {
        return this.networkRepository.getNetworkProperties().map(properties -> {
            if (properties.getPlugins() == null
                || properties.getPlugins().getAggregate() == null
                || properties.getPlugins().getAggregate().getMaxCosignaturesPerAggregate()
                == null) {
                throw new IllegalStateException(
                    "Cannot get maxCosignaturesPerAggregate from network properties.");
            }
            return Integer.parseInt(
                properties.getPlugins().getAggregate().getMaxCosignaturesPerAggregate()
                    .replace("'", ""));
        });
    }

    /**
     * Validate cosignatories against multisig Account(s)
     *
     * @param graphInfo - multisig account graph info
     * @param cosignatories - array of cosignatories extracted from aggregated transaction
     * @param innerTransaction - the inner transaction of the aggregated transaction
     * @return true if the cosignatories are enough to sign.
     */
    private boolean validateCosignatories(MultisigAccountGraphInfo graphInfo,
        Set<Address> cosignatories,
        Transaction innerTransaction) {
        // Validate cosignatories from bottom level to top
        Set<Address> cosignatoriesReceived = new HashSet<>(cosignatories);

        // Check inner transaction. If remove cosigner from multisig account,
        // use minRemoval instead of minApproval for cosignatories validation.

        boolean isMultisigRemoval =
            (innerTransaction.getType() == TransactionType.MULTISIG_ACCOUNT_MODIFICATION)
                && !((MultisigAccountModificationTransaction) innerTransaction)
                .getAddressDeletions()
                .isEmpty();

        Map<Integer, List<MultisigAccountInfo>> storedMap = new TreeMap<>(graphInfo
            .getMultisigEntries());

        return storedMap.values().stream()
            .anyMatch(entry -> entry.stream().allMatch(multisig -> {
                if (multisig.getMinApproval() > 0 && multisig.getMinRemoval() > 0) {
                    // To make sure it is multisig account
                    Set<Address> matchedCosignatories = multisig.getCosignatoryAddresses().stream().collect(
                            Collectors.toSet());
                    matchedCosignatories.retainAll(cosignatoriesReceived);
                    /*
                      if minimal signature requirement met at current level, push the multisig account
                      into the received signatories array for next level validation.
                      Otherwise return validation failed.
                     */
                    if ((matchedCosignatories.size() >= multisig.getMinApproval()
                        && !isMultisigRemoval) || (
                        matchedCosignatories.size() >= multisig.getMinRemoval()
                            && isMultisigRemoval)) {
                        cosignatoriesReceived.add(multisig.getAccountAddress());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }));


    }
}
