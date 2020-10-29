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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.catapult.builders.AccountRestrictionsBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountRestrictionTest {

  @Test
  void shouldCreateAccountRestrictionViaConstructor() {
    Address address = Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1");
    AccountRestriction accountRestrictionOperation =
        new AccountRestriction(
            AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
            Arrays.asList(TransactionType.TRANSFER, TransactionType.MOSAIC_METADATA));
    assertEquals(
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        accountRestrictionOperation.getRestrictionFlags());
    assertEquals(2, accountRestrictionOperation.getValues().size());

    AccountRestrictions accountRestrictions =
        new AccountRestrictions(address, Collections.singletonList(accountRestrictionOperation));

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E101000000000000000440020000000000000054414442";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void shouldCreateAccountRestrictionsInfoViaConstructor() {
    String metaId = "12345";
    Address address = Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1");
    AccountRestriction accountRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_OUTGOING_ADDRESS,
            Collections.singletonList("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQM"));
    AccountRestrictions accountRestrictions =
        new AccountRestrictions(address, Arrays.asList(accountRestriction));
    AccountPropertiesInfo accountPropertiesInfo =
        new AccountPropertiesInfo(metaId, accountRestrictions);

    assertEquals(metaId, accountPropertiesInfo.getMetaId());
    assertEquals(address, accountPropertiesInfo.getAccountRestrictions().getAddress());
    assertEquals(1, accountPropertiesInfo.getAccountRestrictions().getRestrictions().size());
    assertEquals(
        AccountAddressRestrictionFlags.ALLOW_OUTGOING_ADDRESS,
        accountPropertiesInfo
            .getAccountRestrictions()
            .getRestrictions()
            .get(0)
            .getRestrictionFlags());

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1010000000000000001400000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void shouldCreateAccountRestrictionsViaConstructor() {
    Address address = Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1");
    AccountRestriction accountAllowIncomingRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            Arrays.asList("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQM"));
    AccountRestrictions accountRestrictions =
        new AccountRestrictions(address, Arrays.asList(accountAllowIncomingRestriction));

    assertEquals(address, accountRestrictions.getAddress());
    assertEquals(1, accountRestrictions.getRestrictions().size());
    assertEquals(
        AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
        accountRestrictions.getRestrictions().get(0).getRestrictionFlags());

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1010000000000000001000000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void shouldCreateAccountRestrictionViaConstructorMultipleTypes() {
    Address address = Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1");
    AccountRestriction accountRestrictionOperation =
        new AccountRestriction(
            AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
            Arrays.asList(TransactionType.TRANSFER, TransactionType.MOSAIC_METADATA));
    assertEquals(
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        accountRestrictionOperation.getRestrictionFlags());
    assertEquals(2, accountRestrictionOperation.getValues().size());

    AccountRestriction accountAllowIncomingRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            Arrays.asList("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQM"));

    AccountRestrictions accountRestrictions =
        new AccountRestrictions(
            address, Arrays.asList(accountRestrictionOperation, accountAllowIncomingRestriction));

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E10200000000000000044002000000000000005441444201000000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
