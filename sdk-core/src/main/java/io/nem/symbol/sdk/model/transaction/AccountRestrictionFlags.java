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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The valid combinations of {@link AccountRestrictionFlag} that creates any {@link
 * AccountRestrictionFlags}.
 *
 * <p>Type of account restriction types:
 *
 * <p>0x0001 (1 decimal) - Allow only incoming transactions from a given address.
 *
 * <p>0x0002 (2 decimal) - Allow only incoming transactions containing a given mosaic identifier.
 *
 * <p>0x4001 (16385 decimal) - Allow only outgoing transactions to a given address.
 *
 * <p>0x4004 (16388 decimal) - Allow only outgoing transactions with a given transaction type.
 *
 * <p>0x8001 (32769 decimal) - Block incoming transactions from a given address.
 *
 * <p>0x8002 (32770 decimal) - Block incoming transactions containing a given mosaic identifier.
 *
 * <p>0xC001 (49153 decimal) - Block outgoing transactions to a given address.
 *
 * <p>0xC004 (49156 decimal) - Block outgoing transactions with a given transaction type.
 */
public interface AccountRestrictionFlags {

  /**
   * Returns enum value.
   *
   * @return byte
   */
  int getValue();

  /** @return the enum name. */
  String name();

  /** @return a list with the individual flags. */
  List<AccountRestrictionFlag> getFlags();

  /** @return the target type. */
  AccountRestrictionTargetType getTargetType();

  /**
   * Search for all the possible AccountRestrictionFlags
   *
   * @param value Raw value of the enum.
   * @return Enum value.
   */
  static AccountRestrictionFlags rawValueOf(final int value) {
    return values().stream()
        .filter(e -> e.getValue() == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /** @return all the possible AccountRestrictionFlags values. */
  static List<? extends AccountRestrictionFlags> values() {
    Stream<AccountAddressRestrictionFlags> stream1 =
        Arrays.stream(AccountAddressRestrictionFlags.values());
    Stream<AccountOperationRestrictionFlags> stream2 =
        Arrays.stream(AccountOperationRestrictionFlags.values());
    Stream<AccountMosaicRestrictionFlags> stream3 =
        Arrays.stream(AccountMosaicRestrictionFlags.values());
    return Stream.of(stream1, stream2, stream3).flatMap(i -> i).collect(Collectors.toList());
  }
}
