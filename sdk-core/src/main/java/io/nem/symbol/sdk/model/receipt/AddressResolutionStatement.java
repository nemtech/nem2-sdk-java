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
package io.nem.symbol.sdk.model.receipt;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** {@link ResolutionStatement} specific for addresses. */
public class AddressResolutionStatement extends ResolutionStatement<UnresolvedAddress, Address> {

  /**
   * Constructor
   *
   * @param recordId the database id if known.
   * @param height Height
   * @param unresolved an {@link UnresolvedAddress}
   * @param resolutionEntries Array of {@link Address} resolution entries.
   */
  public AddressResolutionStatement(
      String recordId,
      BigInteger height,
      UnresolvedAddress unresolved,
      List<ResolutionEntry<Address>> resolutionEntries) {
    super(recordId, ResolutionType.ADDRESS, height, unresolved, resolutionEntries);
  }

  /**
   * This method tries to resolve the unresolved address using the the resolution entries.
   *
   * @param statements list of statements.
   * @param height the height of the transaction.
   * @param unresolvedAddress the {@link UnresolvedAddress}
   * @param primaryId the primary id
   * @param secondaryId the secondary id
   * @return the {@link Optional} of the resolved {@link Address}
   */
  public static Optional<Address> getResolvedAddress(
      List<AddressResolutionStatement> statements,
      BigInteger height,
      UnresolvedAddress unresolvedAddress,
      long primaryId,
      long secondaryId) {
    if (unresolvedAddress instanceof Address) {
      return Optional.of((Address) unresolvedAddress);
    }
    return statements.stream()
        .filter(s -> height.equals(s.getHeight()))
        .filter(r -> r.getUnresolved().equals(unresolvedAddress))
        .map(
            r -> r.getResolutionEntryById(primaryId, secondaryId).map(ResolutionEntry::getResolved))
        .findFirst()
        .flatMap(Function.identity());
  }
}
