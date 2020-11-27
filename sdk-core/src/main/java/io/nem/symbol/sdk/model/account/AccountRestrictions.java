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

import io.nem.symbol.catapult.builders.AccountRestrictionAddressValueBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionFlagsDto;
import io.nem.symbol.catapult.builders.AccountRestrictionMosaicValueBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionTransactionTypeValueBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionsBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionsInfoBuilder;
import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.EntityTypeDto;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

/** Account properties structure describes property information for an account. */
public class AccountRestrictions {

  /** state version */
  private final int version;
  /** The address where the restrictions apply. */
  private final Address address;

  /** The restrictions. */
  private final List<AccountRestriction> restrictions;

  public AccountRestrictions(int version, Address address, List<AccountRestriction> restrictions) {
    Validate.notNull(address, "address is required");
    Validate.notNull(restrictions, "restrictions is required");
    this.version = version;
    this.address = address;
    this.restrictions = restrictions;
  }

  public Address getAddress() {
    return address;
  }

  public List<AccountRestriction> getRestrictions() {
    return restrictions;
  }

  public int getVersion() {
    return version;
  }

  /** @return serializes the state of this object. */
  public byte[] serialize() {
    AddressDto address = SerializationUtils.toAddressDto(getAddress());
    List<AccountRestrictionsInfoBuilder> restrictions =
        getRestrictions().stream()
            .map(this::toAccountRestrictionsInfoBuilder)
            .collect(Collectors.toList());
    return AccountRestrictionsBuilder.create((short) getVersion(), address, restrictions)
        .serialize();
  }

  private AccountRestrictionsInfoBuilder toAccountRestrictionsInfoBuilder(
      AccountRestriction restriction) {
    EnumSet<AccountRestrictionFlagsDto> restrictionFlags =
        BinarySerializationImpl.toAccountRestrictionsFlagsDto(
            restriction.getRestrictionFlags().getFlags());
    AccountRestrictionAddressValueBuilder addressRestrictions =
        toAccountRestrictionAddressValueBuilder(restriction);
    AccountRestrictionMosaicValueBuilder mosaicIdRestrictions =
        toAccountRestrictionMosaicValueBuilder(restriction);
    AccountRestrictionTransactionTypeValueBuilder transactionTypeRestrictions =
        toAccountRestrictionTransactionTypeValueBuilder(restriction);

    return AccountRestrictionsInfoBuilder.create(
        restrictionFlags, addressRestrictions, mosaicIdRestrictions, transactionTypeRestrictions);
  }

  private AccountRestrictionAddressValueBuilder toAccountRestrictionAddressValueBuilder(
      AccountRestriction restriction) {
    return AccountRestrictionAddressValueBuilder.create(
        restriction.getValues().stream()
            .filter(o -> o instanceof Address)
            .map(o -> SerializationUtils.toAddressDto((Address) o))
            .collect(Collectors.toList()));
  }

  private AccountRestrictionMosaicValueBuilder toAccountRestrictionMosaicValueBuilder(
      AccountRestriction restriction) {
    return AccountRestrictionMosaicValueBuilder.create(
        restriction.getValues().stream()
            .filter(o -> o instanceof MosaicId)
            .map(o -> SerializationUtils.toMosaicIdDto((MosaicId) o))
            .collect(Collectors.toList()));
  }

  private AccountRestrictionTransactionTypeValueBuilder
      toAccountRestrictionTransactionTypeValueBuilder(AccountRestriction restriction) {
    return AccountRestrictionTransactionTypeValueBuilder.create(
        restriction.getValues().stream()
            .filter(o -> o instanceof TransactionType)
            .map(o -> EntityTypeDto.rawValueOf((short) ((TransactionType) o).getValue()))
            .collect(Collectors.toList()));
  }
}
