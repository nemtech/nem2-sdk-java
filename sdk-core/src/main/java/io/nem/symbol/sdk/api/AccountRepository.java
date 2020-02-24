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

package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.reactivex.Observable;
import java.util.List;

/**
 * Account interface repository.
 *
 * @since 1.0
 */
public interface AccountRepository {

    /**
     * Gets an AccountInfo for an account.
     *
     * @param address Address
     * @return Observable {@link AccountInfo}
     */
    Observable<AccountInfo> getAccountInfo(Address address);

    /**
     * Gets AccountsInfo for different accounts based on their addresses.
     *
     * @param addresses {@link List} of {@link Address}
     * @return Observable {@link List} of {@link AccountInfo}
     */
    Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses);

    /**
     * Gets an list of confirmed transactions for which an account is signer or receiver.
     *
     * @param publicAccount PublicAccount
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> transactions(PublicAccount publicAccount);

    /**
     * Gets an list of confirmed transactions for which an account is signer or receiver. With
     * pagination.
     *
     * @param publicAccount PublicAccount
     * @param criteria the criteria used to filter and order transactions
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> transactions(PublicAccount publicAccount,
        TransactionSearchCriteria criteria);

    /**
     * Gets an list of transactions for which an account is the recipient of a transaction. A
     * transaction is said to be incoming with respect to an account if the account is the recipient
     * of a transaction.
     *
     * @param publicAccount {@link PublicAccount}
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount);

    /**
     * Gets an list of transactions for which an account is the recipient of a transaction. A
     * transaction is said to be incoming with respect to an account if the account is the recipient
     * of a transaction. With pagination.
     *
     * @param publicAccount PublicAccount
     * @param criteria the criteria used to filter and order transactions
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria);

    /**
     * Gets an list of transactions for which an account is the sender a transaction. A transaction
     * is said to be outgoing with respect to an account if the account is the sender of a
     * transaction.
     *
     * @param publicAccount PublicAccount
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount);

    /**
     * Gets an list of transactions for which an account is the sender a transaction. A transaction
     * is said to be outgoing with respect to an account if the account is the sender of a
     * transaction. With pagination.
     *
     * @param publicAccount PublicAccount
     * @param criteria the criteria used to filter and order transactions
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria);

    /**
     * Gets an list of aggregate bonded transactions where the account is the sender or requires to
     * cosign the transaction.
     *
     * @param publicAccount the sender account
     * @return an observable list of Transaction
     */
    Observable<List<Transaction>> partialTransactions(PublicAccount publicAccount);

    /**
     * Gets an list of aggregate bonded transactions where the account is the sender or requires to
     * cosign the transaction.
     *
     * @param publicAccount the sender account
     * @param criteria the criteria with the pagination, ordering, filtering, etc
     * @return an observable list of Transaction
     */
    Observable<List<Transaction>> partialTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria);

    /**
     * Gets an list of transactions for which an account is the sender or has sign the transaction.
     * A transaction is said to be aggregate bonded with respect to an account if there are missing
     * signatures.
     *
     * @param publicAccount PublicAccount
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<AggregateTransaction>> aggregateBondedTransactions(PublicAccount publicAccount);

    /**
     * Gets an list of transactions for which an account is the sender or has sign the transaction.
     * A transaction is said to be aggregate bonded with respect to an account if there are missing
     * signatures. With pagination.
     *
     * @param publicAccount PublicAccount
     * @param criteria the criteria used to filter and order transactions
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria);

    /**
     * Gets the list of transactions for which an account is the sender or receiver and which have
     * not yet been included in a block. Unconfirmed transactions are those transactions that have
     * not yet been included in a block. Unconfirmed transactions are not guaranteed to be included
     * in any block.
     *
     * @param publicAccount PublicAccount
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount);

    /**
     * Gets the list of transactions for which an account is the sender or receiver and which have
     * not yet been included in a block. Unconfirmed transactions are those transactions that have
     * not yet been included in a block. Unconfirmed transactions are not guaranteed to be included
     * in any block. With pagination.
     *
     * @param publicAccount PublicAccount
     * @param criteria the criteria used to filter and order transactions
     * @return Observable {@link List} of {@link Transaction}
     */
    Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria);
}
