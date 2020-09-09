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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.catapult.builders.EntityTypeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TransactionTypeTest {

  @Test
  void aggregateCompleteType() {
    TransactionType transactionType = TransactionType.AGGREGATE_COMPLETE;
    assertEquals(0x4141, transactionType.getValue());
    assertEquals(16705, transactionType.getValue());
    assertEquals(TransactionType.AGGREGATE_COMPLETE, TransactionType.rawValueOf(16705));
  }

  @Test
  void aggregateBondedType() {
    TransactionType transactionType = TransactionType.AGGREGATE_BONDED;
    assertEquals(0x4241, transactionType.getValue());
    assertEquals(16961, transactionType.getValue());
    assertEquals(TransactionType.AGGREGATE_BONDED, TransactionType.rawValueOf(16961));
  }

  @Test
  void mosaicCreationType() {
    TransactionType transactionType = TransactionType.MOSAIC_DEFINITION;
    assertEquals(0x414d, transactionType.getValue());
    assertEquals(16717, transactionType.getValue());
    assertEquals(TransactionType.MOSAIC_DEFINITION, TransactionType.rawValueOf(16717));
  }

  @Test
  void mosaicSupplyChangeType() {
    TransactionType transactionType = TransactionType.MOSAIC_SUPPLY_CHANGE;
    assertEquals(0x424d, transactionType.getValue());
    assertEquals(16973, transactionType.getValue());
    assertEquals(TransactionType.MOSAIC_SUPPLY_CHANGE, TransactionType.rawValueOf(16973));
  }

  @Test
  void multisigModificationAction() {
    TransactionType transactionType = TransactionType.MULTISIG_ACCOUNT_MODIFICATION;
    assertEquals(0x4155, transactionType.getValue());
    assertEquals(16725, transactionType.getValue());
    assertEquals(TransactionType.MULTISIG_ACCOUNT_MODIFICATION, TransactionType.rawValueOf(16725));
  }

  @Test
  void namespaceCreationType() {
    TransactionType transactionType = TransactionType.NAMESPACE_REGISTRATION;
    assertEquals(0x414e, transactionType.getValue());
    assertEquals(16718, transactionType.getValue());
    assertEquals(TransactionType.NAMESPACE_REGISTRATION, TransactionType.rawValueOf(16718));
  }

  @Test
  void transferType() {
    TransactionType transactionType = TransactionType.TRANSFER;
    assertEquals(0x4154, transactionType.getValue());
    assertEquals(16724, transactionType.getValue());
    assertEquals(TransactionType.TRANSFER, TransactionType.rawValueOf(16724));
  }

  @Test
  void lockFundsType() {
    TransactionType transactionType = TransactionType.HASH_LOCK;
    assertEquals(0x4148, transactionType.getValue());
    assertEquals(16712, transactionType.getValue());
    assertEquals(TransactionType.HASH_LOCK, TransactionType.rawValueOf(16712));
  }

  @Test
  void secretLockType() {
    TransactionType transactionType = TransactionType.SECRET_LOCK;
    assertEquals(0x4152, transactionType.getValue());
    assertEquals(16722, transactionType.getValue());
    assertEquals(TransactionType.SECRET_LOCK, TransactionType.rawValueOf(16722));
  }

  @Test
  void secretProofType() {
    TransactionType transactionType = TransactionType.SECRET_PROOF;
    assertEquals(0x4252, transactionType.getValue());
    assertEquals(16978, transactionType.getValue());
    assertEquals(TransactionType.SECRET_PROOF, TransactionType.rawValueOf(16978));
  }

  @Test
  void addressAliasType() {
    TransactionType transactionType = TransactionType.ADDRESS_ALIAS;
    assertEquals(0x424E, transactionType.getValue());
    assertEquals(16974, transactionType.getValue());
    assertEquals(TransactionType.ADDRESS_ALIAS, TransactionType.rawValueOf(16974));
  }

  @Test
  void mosaicAliasType() {
    TransactionType transactionType = TransactionType.MOSAIC_ALIAS;
    assertEquals(0x434E, transactionType.getValue());
    assertEquals(17230, transactionType.getValue());
    assertEquals(TransactionType.MOSAIC_ALIAS, TransactionType.rawValueOf(17230));
  }

  @Test
  void accountPropertiesAddressType() {
    TransactionType transactionType = TransactionType.ACCOUNT_ADDRESS_RESTRICTION;
    assertEquals(0x4150, transactionType.getValue());
    assertEquals(16720, transactionType.getValue());
    assertEquals(TransactionType.ACCOUNT_ADDRESS_RESTRICTION, TransactionType.rawValueOf(16720));
  }

  @Test
  void accountPropertiesMosaic() {
    TransactionType transactionType = TransactionType.ACCOUNT_MOSAIC_RESTRICTION;
    assertEquals(0x4250, transactionType.getValue());
    assertEquals(16976, transactionType.getValue());
    assertEquals(TransactionType.ACCOUNT_MOSAIC_RESTRICTION, TransactionType.rawValueOf(16976));
  }

  @Test
  void accountPropertiesEntityType() {
    TransactionType transactionType = TransactionType.ACCOUNT_OPERATION_RESTRICTION;
    assertEquals(0x4350, transactionType.getValue());
    assertEquals(17232, transactionType.getValue());
    assertEquals(TransactionType.ACCOUNT_OPERATION_RESTRICTION, TransactionType.rawValueOf(17232));
  }

  @Test
  void accountLinkType() {
    TransactionType transactionType = TransactionType.ACCOUNT_KEY_LINK;
    assertEquals(0x414C, transactionType.getValue());
    assertEquals(16716, transactionType.getValue());
    assertEquals(TransactionType.ACCOUNT_KEY_LINK, TransactionType.rawValueOf(16716));
  }

  @ParameterizedTest
  @EnumSource(TransactionType.class)
  void validTransactionTypeEnumValues(TransactionType transactionType) {

    Assertions.assertNotNull(
        EntityTypeDto.rawValueOf((short) transactionType.getValue()),
        transactionType.getValue() + " not found. Transaction " + transactionType.getValue());
  }

  @ParameterizedTest
  @EnumSource(EntityTypeDto.class)
  void validEntityTypeDtoEnumValue(EntityTypeDto enumTypeDto) {
    if (enumTypeDto == EntityTypeDto.RESERVED) {
      return;
    }
    if (enumTypeDto == EntityTypeDto.BLOCK_HEADER_BUILDER) {
      return;
    }
    Assertions.assertNotNull(
        TransactionType.rawValueOf(enumTypeDto.getValue()),
        enumTypeDto.getValue() + " not found. Transaction " + enumTypeDto.getValue());
  }
}
