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

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.MultisigEntryBuilder;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.Stored;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

/**
 * The multisig account info structure describes information of a multisig account.
 *
 * @since 1.0
 */
public class MultisigAccountInfo implements Stored {

  /** state version */
  private final int version;
  /** The stored database. */
  private final String recordId;

  private final Address accountAddress;
  private final long minApproval;
  private final long minRemoval;
  private final List<Address> cosignatoryAddresses;
  private final List<Address> multisigAddresses;

  public MultisigAccountInfo(
      String recordId,
      int version,
      Address accountAddress,
      long minApproval,
      long minRemoval,
      List<Address> cosignatories,
      List<Address> multisigAddresses) {
    Validate.notNull(accountAddress, "accountAddress is required");
    Validate.notNull(cosignatories, "cosignatories is required");
    Validate.notNull(multisigAddresses, "multisigAddresses is required");
    this.version = version;
    this.recordId = recordId;
    this.accountAddress = accountAddress;
    this.minApproval = minApproval;
    this.minRemoval = minRemoval;
    this.cosignatoryAddresses = cosignatories;
    this.multisigAddresses = multisigAddresses;
  }

  /**
   * Returns account multisig public account.
   *
   * @return PublicAccount
   */
  public Address getAccountAddress() {
    return accountAddress;
  }

  /**
   * Returns number of signatures needed to approve a transaction.
   *
   * @return int
   */
  public long getMinApproval() {
    return minApproval;
  }

  /**
   * Returns number of signatures needed to remove a cosignatory.
   *
   * @return int
   */
  public long getMinRemoval() {
    return minRemoval;
  }

  /**
   * Returns multisig account cosignatories.
   *
   * @return List of {@link PublicAccount}
   */
  public List<Address> getCosignatoryAddresses() {
    return cosignatoryAddresses;
  }

  /**
   * Returns multisig accounts this account is cosigner of.
   *
   * @return List of {@link PublicAccount}
   */
  public List<Address> getMultisigAddresses() {
    return multisigAddresses;
  }

  /**
   * Checks if an account is cosignatory of the multisig account.
   *
   * @param account PublicAccount
   * @return boolean
   */
  public boolean hasCosigner(Address account) {
    return this.cosignatoryAddresses.contains(account);
  }

  /**
   * Checks if the multisig account is cosignatory of an account.
   *
   * @param account PublicAccount
   * @return boolean
   */
  public boolean isCosignerOfMultisigAccount(Address account) {
    return this.multisigAddresses.contains(account);
  }

  /** @return state version */
  public int getVersion() {
    return version;
  }

  @Override
  public Optional<String> getRecordId() {
    return Optional.ofNullable(this.recordId);
  }

  /**
   * Checks if the account is a multisig account.
   *
   * @return boolean
   */
  public boolean isMultisig() {
    return minApproval != 0 && minRemoval != 0;
  }

  /** @return serializes the state of this object. */
  public byte[] serialize() {
    int minApproval = (int) getMinApproval();
    int minRemoval = (int) getMinRemoval();
    AddressDto accountAddress = SerializationUtils.toAddressDto(getAccountAddress());
    List<AddressDto> cosignatoryAddresses =
        getCosignatoryAddresses().stream()
            .map(SerializationUtils::toAddressDto)
            .collect(Collectors.toList());
    List<AddressDto> multisigAddresses =
        getMultisigAddresses().stream()
            .map(SerializationUtils::toAddressDto)
            .collect(Collectors.toList());
    return MultisigEntryBuilder.create(
            (short) getVersion(),
            minApproval,
            minRemoval,
            accountAddress,
            cosignatoryAddresses,
            multisigAddresses)
        .serialize();
  }
}
