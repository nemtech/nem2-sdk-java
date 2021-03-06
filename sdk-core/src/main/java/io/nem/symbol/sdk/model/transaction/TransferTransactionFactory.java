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

import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;

/** Factory of {@link TransferTransaction} */
public class TransferTransactionFactory extends TransactionFactory<TransferTransaction> {

  /** The required recipient. */
  private final UnresolvedAddress recipient;

  /** The mosaic to be transfered. */
  private final List<Mosaic> mosaics;

  /** An optional message. */
  private Message message;

  private TransferTransactionFactory(
      final NetworkType networkType,
      final Deadline deadline,
      final UnresolvedAddress recipient,
      final List<Mosaic> mosaics) {
    super(TransactionType.TRANSFER, networkType, deadline);
    Validate.notNull(recipient, "Recipient must not be null");
    Validate.notNull(mosaics, "Mosaics must not be null");
    this.recipient = recipient;
    this.mosaics = mosaics;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param deadline the deadline
   * @param recipient Recipient address.
   * @param mosaics List of mosaics.
   * @return Transfer transaction.
   */
  public static TransferTransactionFactory create(
      final NetworkType networkType,
      final Deadline deadline,
      final UnresolvedAddress recipient,
      final List<Mosaic> mosaics) {
    return new TransferTransactionFactory(networkType, deadline, recipient, mosaics);
  }

  /**
   * Creates a TransferTransactionFactory with special message payload for persistent harvesting
   * delegation unlocking
   *
   * @param networkType The network type.
   * @param deadline the deadline
   * @param signingPrivateKey Remote harvester signing private key linked to the main account
   * @param vrfPrivateKey VRF private key linked to the main account
   * @param nodePublicKey Recipient public key
   * @return {@link TransferTransactionFactory}
   */
  public static TransferTransactionFactory createPersistentDelegationRequestTransaction(
      NetworkType networkType,
      Deadline deadline,
      PrivateKey signingPrivateKey,
      PrivateKey vrfPrivateKey,
      PublicKey nodePublicKey) {
    PersistentHarvestingDelegationMessage message =
        PersistentHarvestingDelegationMessage.create(
            signingPrivateKey, vrfPrivateKey, nodePublicKey);
    return new TransferTransactionFactory(
            networkType,
            deadline,
            Address.createFromPublicKey(nodePublicKey.toHex(), networkType),
            Collections.emptyList())
        .message(message);
  }

  /**
   * Returns address of the recipient.
   *
   * @return recipient address
   */
  public UnresolvedAddress getRecipient() {
    return recipient;
  }

  /**
   * Returns list of mosaic objects.
   *
   * @return List of {@link Mosaic}
   */
  public List<Mosaic> getMosaics() {
    return mosaics;
  }

  /**
   * Returns transaction message.
   *
   * @return Message.
   */
  public Message getMessage() {
    return message;
  }

  /**
   * It sets the message if required.
   *
   * @param message the message to be set.
   * @return this factory
   */
  public TransferTransactionFactory message(Message message) {
    this.message = message;
    return this;
  }

  @Override
  public TransferTransaction build() {
    return new TransferTransaction(this);
  }
}
