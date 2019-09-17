/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.account;

import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import java.util.List;

/**
 * The account structure describes an account private key, public key, address and allows signing
 * transactions.
 *
 * @since 1.0
 */
public class Account {

    private final KeyPair keyPair;
    private final PublicAccount publicAccount;

    /**
     * Constructor
     *
     * @param privateKey String
     * @param networkType {@link NetworkType}
     * @param signSchema the {@link SignSchema}
     */
    public Account(String privateKey, NetworkType networkType, SignSchema signSchema) {
        this.keyPair = new KeyPair(PrivateKey.fromHexString(privateKey));
        this.publicAccount = new PublicAccount(this.getPublicKey(), networkType, signSchema);
    }

    /**
     * Constructor
     *
     * @param keyPair the public private key pair.
     * @param networkType {@link NetworkType}
     * @param signSchema the {@link SignSchema}
     */
    public Account(KeyPair keyPair, NetworkType networkType, SignSchema signSchema) {
        this.keyPair = keyPair;
        this.publicAccount = new PublicAccount(this.getPublicKey(), networkType, signSchema);
    }

    /**
     * Create an Account from a given private key.
     *
     * @param privateKey Private key from an account
     * @param networkType {@link NetworkType}
     * @param signSchema the {@link SignSchema}
     * @return {@link Account}
     */
    public static Account createFromPrivateKey(String privateKey, NetworkType networkType,
        SignSchema signSchema) {
        return new Account(privateKey, networkType, signSchema);
    }

    /**
     * It generates a new account for the given network.
     */
    public static Account generateNewAccount(NetworkType networkType, SignSchema signSchema) {
        KeyPair keyPair = new KeyPair();
        return new Account(keyPair.getPrivateKey().toString(), networkType, signSchema);
    }

    /**
     * Account public key.
     *
     * @return {@link String}
     */
    public String getPublicKey() {
        return this.keyPair.getPublicKey().toString();
    }

    /**
     * Account private key.
     *
     * @return {@link String}
     */
    public String getPrivateKey() {
        return this.keyPair.getPrivateKey().toString().toUpperCase();
    }

    /**
     * Account keyPair containing public and private key.
     *
     * @return {@link KeyPair}
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Account address.
     *
     * @return {@link Address}
     */
    public Address getAddress() {
        return this.publicAccount.getAddress();
    }

    /**
     * Public account.
     *
     * @return {@link PublicAccount}
     */
    public PublicAccount getPublicAccount() {
        return publicAccount;
    }

    /**
     * Sign a transaction.
     *
     * @param transaction The transaction to be signed.
     * @return {@link SignedTransaction}
     */
    public SignedTransaction sign(final Transaction transaction, final String generationHash) {
        return transaction.signWith(this, generationHash);
    }

    /**
     * Sign aggregate signature transaction.
     *
     * @param cosignatureTransaction The aggregate signature transaction.
     * @return {@link CosignatureSignedTransaction}
     */
    public CosignatureSignedTransaction signCosignatureTransaction(
        CosignatureTransaction cosignatureTransaction) {
        return cosignatureTransaction.signWith(this);
    }

    /**
     * Sign transaction with cosignatories creating a new SignedTransaction.
     *
     * @param transaction The aggregate transaction to be signed.
     * @param cosignatories The list of accounts that will cosign the transaction
     * @return {@link SignedTransaction}
     */
    public SignedTransaction signTransactionWithCosignatories(
        final AggregateTransaction transaction,
        final List<Account> cosignatories,
        final String generationHash) {
        return transaction.signTransactionWithCosigners(this, cosignatories, generationHash);
    }
}
