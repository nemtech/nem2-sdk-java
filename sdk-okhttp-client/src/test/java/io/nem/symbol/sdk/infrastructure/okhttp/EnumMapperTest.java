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
package io.nem.symbol.sdk.infrastructure.okhttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.nem.symbol.catapult.builders.AliasActionDto;
import io.nem.symbol.catapult.builders.LockHashAlgorithmDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.symbol.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.symbol.catapult.builders.NamespaceRegistrationTypeDto;
import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.model.account.AccountKeyType;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.message.MessageType;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.AliasType;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.node.NodeStatus;
import io.nem.symbol.sdk.model.node.RoleType;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.SecretHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.TransactionState;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountKeyTypeFlagsEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionFlagsEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasActionEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockOrderByEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.LinkActionEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MessageTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionEntryTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicSupplyChangeActionEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NodeIdentityEqualityStrategy;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NodeStatusEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.PositionEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ReceiptTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.RolesTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretHashAlgorithmEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionGroupEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionTypeEnum;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** This class tests that the model and dto enums have the same values. */
public class EnumMapperTest {

  @Test
  void testReceiptTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(ReceiptTypeEnum.values())
        .forEach(
            v -> {
              assertNotNull(ReceiptType.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testReceiptTypeEnumDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(ReceiptType.values())
        .forEach(
            v -> {
              assertNotNull(ReceiptTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testLinkActionEnumDTO() {
    Set<Byte> existingValues = new HashSet<>();
    Arrays.stream(LinkAction.values())
        .forEach(
            v -> {
              assertNotNull(LinkActionEnum.fromValue((int) v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  public void shouldAccountRestrictionTypeEnumMapToAccountRestrictionType() {
    Arrays.stream(AccountRestrictionFlagsEnum.values())
        .forEach(v -> assertNotNull(AccountRestrictionFlags.rawValueOf(v.getValue())));
  }

  @Test
  public void shouldAccountRestrictionTypeMapToAccountRestrictionType() {
    AccountRestrictionFlags.values().stream()
        .forEach(
            v ->
                Assertions.assertNotNull(
                    AccountRestrictionFlagsEnum.fromValue((int) v.getValue())));
  }

  @Test
  void testFromLinkActionEnumToAccountLinkAction() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(LinkActionEnum.values())
        .forEach(
            v -> {
              assertNotNull(LinkAction.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAliasActionModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(AliasActionEnum.values())
        .forEach(
            v -> {
              assertNotNull(AliasAction.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAliasActionCatbuffer() {
    Set<Byte> existingValues = new HashSet<>();
    Arrays.stream(AliasAction.values())
        .forEach(
            v -> {
              assertNotNull(AliasActionDto.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAliasActionDTO() {
    Set<Byte> existingValues = new HashSet<>();
    Arrays.stream(AliasAction.values())
        .forEach(
            v -> {
              assertNotNull(AliasActionEnum.fromValue((int) v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testNamespaceRegistrationTypeDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(NamespaceRegistrationType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  NamespaceRegistrationTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testNamespaceRegistrationTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(NamespaceRegistrationTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  NamespaceRegistrationType.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testNamespaceRegistrationTypeCatbuffer() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(NamespaceRegistrationTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  NamespaceRegistrationTypeDto.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testTransactionTypeDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(TransactionType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(TransactionTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testTransactionTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(TransactionTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(TransactionType.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAccountRestrictionModificationDTO() {
    Set<Integer> existingValues = new HashSet<>();
    AccountRestrictionFlags.values().stream()
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  AccountRestrictionFlagsEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAccountTypeDto() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(AccountType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(AccountTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAccountTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(AccountTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(AccountType.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAccountRestrictionTypeDTO() {
    Set<Integer> existingValues = new HashSet<>();
    AccountRestrictionFlags.values().stream()
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  AccountRestrictionFlagsEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testAccountRestrictionTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(AccountRestrictionFlagsEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(AccountRestrictionFlags.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testLockHashAlgorithmDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(SecretHashAlgorithm.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(SecretHashAlgorithmEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testLockHashAlgorithmTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(SecretHashAlgorithmEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  SecretHashAlgorithm.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testLockHashAlgorithmTypeCatbuffer() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(SecretHashAlgorithm.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  LockHashAlgorithmDto.rawValueOf((byte) v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionType.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionTypeDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionTypeDto.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicSupplyChangeActionEnumCatbuffer() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicSupplyChangeActionType.values())
        .forEach(
            v -> {
              assertNotNull(MosaicSupplyChangeActionDto.rawValueOf((byte) v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicSupplyChangeActionEnumDTO() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicSupplyChangeActionType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicSupplyChangeActionEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void MosaicSupplyChangeActionTypeModel() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicSupplyChangeActionEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicSupplyChangeActionType.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionTypeEnumToMosaicRestrictionType() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionType.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionTypeToMosaicRestrictionTypeEnum() {
    Set<Byte> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionTypeEnum.fromValue((int) v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionEntryTypeEnumToMosaicRestrictionEntryType() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionEntryTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionType.rawValueOf(v.getValue().byteValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMosaicRestrictionEntryTypeToMosaicRestrictionEntryTypeEnum() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MosaicRestrictionEntryType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(
                  MosaicRestrictionEntryTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMessageTypeEnumToMessageType() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MessageTypeEnum.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(MessageType.rawValueOf(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @Test
  void testMessageTypeToMessageTypeEnum() {
    Set<Integer> existingValues = new HashSet<>();
    Arrays.stream(MessageType.values())
        .forEach(
            v -> {
              Assertions.assertNotNull(MessageTypeEnum.fromValue(v.getValue()), v.name());
              Assertions.assertTrue(
                  existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
            });
  }

  @ParameterizedTest
  @EnumSource(TransactionState.class)
  void validFromTransactionState(TransactionState transactionState) {
    assertNotNull(TransactionGroupEnum.fromValue(transactionState.getValue()));
    assertNotNull(TransactionGroupEnum.valueOf(transactionState.name()));
    Assertions.assertEquals(
        TransactionGroupEnum.fromValue(transactionState.getValue()).getValue(),
        transactionState.getValue());
  }

  @ParameterizedTest
  @EnumSource(TransactionGroupEnum.class)
  void validFromTransactionStateTypeEnum(TransactionGroupEnum transactionState) {
    assertNotNull(TransactionState.valueOf(transactionState.name()));
    assertNotNull(TransactionState.rawValueOf(transactionState.getValue()));
    Assertions.assertEquals(
        TransactionState.rawValueOf(transactionState.getValue()).getValue(),
        transactionState.getValue());
  }

  @ParameterizedTest
  @EnumSource(RoleType.class)
  void validFromRoleType(RoleType roleType) {
    assertNotNull(RolesTypeEnum.fromValue(roleType.getValue()));
    Assertions.assertEquals(
        RolesTypeEnum.fromValue(roleType.getValue()).getValue(), roleType.getValue());
  }

  @ParameterizedTest
  @EnumSource(RolesTypeEnum.class)
  void validRoleTypeEnum(RolesTypeEnum rolesType) {
    assertNotNull(RoleType.rawValueOf(rolesType.getValue()));
    Assertions.assertEquals(
        RolesTypeEnum.fromValue(rolesType.getValue()).getValue(), rolesType.getValue());
  }

  @ParameterizedTest
  @EnumSource(NodeStatus.class)
  void validFromNodeStatus(NodeStatus nodeStatus) {
    assertNotNull(NodeStatusEnum.fromValue(nodeStatus.getValue()));
    Assertions.assertEquals(
        NodeStatusEnum.fromValue(nodeStatus.getValue()).getValue(), nodeStatus.getValue());
  }

  @ParameterizedTest
  @EnumSource(NodeStatusEnum.class)
  void validNodeStatusEnum(NodeStatusEnum rolesType) {
    assertNotNull(NodeStatus.rawValueOf(rolesType.getValue()));
    Assertions.assertEquals(
        NodeStatusEnum.fromValue(rolesType.getValue()).getValue(), rolesType.getValue());
  }

  @ParameterizedTest
  @EnumSource(Position.class)
  void validFromPosition(Position enumValue) {
    assertNotNull(PositionEnum.fromValue(enumValue.getValue()));
    assertEquals(PositionEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(PositionEnum.class)
  void validPosition(PositionEnum enumValue) {
    assertNotNull(Position.rawValueOf(enumValue.getValue()));
    assertEquals(PositionEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(AliasType.class)
  void validFromAliasType(AliasType enumValue) {
    assertNotNull(AliasTypeEnum.fromValue(enumValue.getValue()));
    assertEquals(AliasTypeEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(AliasTypeEnum.class)
  void validAliasTypeEnum(AliasTypeEnum enumValue) {
    assertNotNull(AliasType.rawValueOf(enumValue.getValue()));
    Assertions.assertEquals(
        AliasTypeEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(AccountKeyType.class)
  void validFromKeyType(AccountKeyType enumValue) {
    assertNotNull(AccountKeyTypeFlagsEnum.fromValue(enumValue.getValue()));
    assertEquals(
        AccountKeyTypeFlagsEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(AccountKeyTypeFlagsEnum.class)
  void validAccountKeyTypeFlagsEnum(AccountKeyTypeFlagsEnum enumValue) {
    assertNotNull(AccountKeyType.rawValueOf(enumValue.getValue()));
    Assertions.assertEquals(
        AccountKeyTypeFlagsEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(io.nem.symbol.sdk.model.network.NodeIdentityEqualityStrategy.class)
  void validFromNodeIdentityEqualityStrategy(
      io.nem.symbol.sdk.model.network.NodeIdentityEqualityStrategy enumValue) {
    assertNotNull(NodeIdentityEqualityStrategy.fromValue(enumValue.getValue()));
    assertEquals(
        NodeIdentityEqualityStrategy.fromValue(enumValue.getValue()).getValue(),
        enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(NodeIdentityEqualityStrategy.class)
  void validNodeIdentityEqualityStrategyEnum(NodeIdentityEqualityStrategy enumValue) {
    assertNotNull(
        io.nem.symbol.sdk.model.network.NodeIdentityEqualityStrategy.rawValueOf(
            enumValue.getValue()));
    Assertions.assertEquals(
        NodeIdentityEqualityStrategy.fromValue(enumValue.getValue()).getValue(),
        enumValue.getValue());
  }

  @ParameterizedTest
  @EnumSource(BlockOrderBy.class)
  void validFromNodeIdentityEqualityStrategy(BlockOrderBy enumValue) {
    assertNotNull(BlockOrderByEnum.fromValue(enumValue.getValue()));
    assertEquals(BlockOrderByEnum.fromValue(enumValue.getValue()).getValue(), enumValue.getValue());
  }
}
