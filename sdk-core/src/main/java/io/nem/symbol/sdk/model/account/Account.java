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
package io.nem.symbol.sdk.model.account;

import io.nem.symbol.core.crypto.CryptoEngines;
import io.nem.symbol.core.crypto.DsaSigner;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
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

  private final NetworkType networkType;

  /**
   * Constructor
   *
   * @param privateKey String
   * @param networkType NetworkType
   */
  public Account(String privateKey, NetworkType networkType) {
    this.keyPair = KeyPair.fromPrivate(PrivateKey.fromHexString(privateKey));
    this.publicAccount = new PublicAccount(this.getPublicKey(), networkType);
    this.networkType = networkType;
  }

  public Account(KeyPair keyPair, NetworkType networkType) {
    this.keyPair = keyPair;
    this.publicAccount = new PublicAccount(this.getPublicKey(), networkType);
    this.networkType = networkType;
  }

  /**
   * Create an Account from a given private key.
   *
   * @param privateKey Private key from an account
   * @param networkType NetworkType
   * @return {@link Account}
   */
  public static Account createFromPrivateKey(String privateKey, NetworkType networkType) {
    return new Account(privateKey, networkType);
  }

  /**
   * Generates an new Account for provided network type
   *
   * @param networkType the network type
   * @return the account.
   */
  public static Account generateNewAccount(NetworkType networkType) {
    return new Account(KeyPair.random(), networkType);
  }

  /**
   * Account public key.
   *
   * @return {@link String}
   */
  public String getPublicKey() {
    return this.keyPair.getPublicKey().toHex();
  }

  /**
   * Account private key.
   *
   * @return {@link String}
   */
  public String getPrivateKey() {
    return this.keyPair.getPrivateKey().toHex();
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
   * @param generationHash the generation hash.
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
   * Creates a CosignatureSignedTransaction from a hash.
   *
   * @param transactionHash The transaction hash
   * @return {@link CosignatureSignedTransaction}
   */
  public CosignatureSignedTransaction signCosignatureTransaction(String transactionHash) {
    DsaSigner signer = CryptoEngines.defaultEngine().createDsaSigner(this.getKeyPair());
    byte[] bytes = ConvertUtils.fromHexToBytes(transactionHash);
    byte[] signatureBytes = signer.sign(bytes).getBytes();
    return new CosignatureSignedTransaction(
        AggregateTransactionCosignature.DEFAULT_VERSION,
        transactionHash,
        ConvertUtils.toHex(signatureBytes),
        this.getPublicAccount());
  }

  /**
   * Sign transaction with cosignatories creating a new SignedTransaction.
   *
   * @param transaction The aggregate transaction to be signed.
   * @param cosignatories The list of accounts that will cosign the transaction
   * @param generationHash the generation hash.
   * @return {@link SignedTransaction}
   */
  public SignedTransaction signTransactionWithCosignatories(
      final AggregateTransaction transaction,
      final List<Account> cosignatories,
      final String generationHash) {
    return transaction.signTransactionWithCosigners(this, cosignatories, generationHash);
  }

  /**
   * Sign transaction with cosignatures creating a new SignedTransaction.
   *
   * @param transaction The aggregate transaction to be signed.
   * @param cosignatures The list of precreated cosignatures
   * @param generationHash the generation hash.
   * @return {@link SignedTransaction}
   */
  public SignedTransaction signTransactionGivenSignatures(
      final AggregateTransaction transaction,
      final List<AggregateTransactionCosignature> cosignatures,
      final String generationHash) {
    return transaction.signTransactionGivenSignatures(this, cosignatures, generationHash);
  }

  public NetworkType getNetworkType() {
    return networkType;
  }
}
