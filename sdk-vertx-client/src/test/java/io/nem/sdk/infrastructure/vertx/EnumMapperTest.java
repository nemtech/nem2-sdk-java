/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.catapult.builders.AliasActionDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.catapult.builders.NamespaceRegistrationTypeDto;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.model.receipt.ReceiptType;
import io.nem.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AccountLinkActionEnum;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionModificationActionEnum;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionTypeEnum;
import io.nem.sdk.openapi.vertx.model.AccountTypeEnum;
import io.nem.sdk.openapi.vertx.model.AliasActionEnum;
import io.nem.sdk.openapi.vertx.model.CosignatoryModificationActionEnum;
import io.nem.sdk.openapi.vertx.model.LockHashAlgorithmEnum;
import io.nem.sdk.openapi.vertx.model.MessageTypeEnum;
import io.nem.sdk.openapi.vertx.model.MosaicRestrictionEntryTypeEnum;
import io.nem.sdk.openapi.vertx.model.MosaicRestrictionTypeEnum;
import io.nem.sdk.openapi.vertx.model.MosaicSupplyChangeActionEnum;
import io.nem.sdk.openapi.vertx.model.NamespaceRegistrationTypeEnum;
import io.nem.sdk.openapi.vertx.model.ReceiptTypeEnum;
import io.nem.sdk.openapi.vertx.model.TransactionTypeEnum;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This class tests that the model and dto enums have the same values.
 */
public class EnumMapperTest {

    @Test
    void testReceiptTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(ReceiptTypeEnum.values()).forEach(v -> {
            Assertions.assertNotNull(ReceiptType.rawValueOf(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testReceiptTypeEnumDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(ReceiptType.values()).forEach(v -> {
            Assertions.assertNotNull(ReceiptTypeEnum.fromValue(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAccountLinkActionEnumDTO() {
        Set<Byte> existingValues = new HashSet<>();
        Arrays.stream(AccountLinkAction.values()).forEach(v -> {
            Assertions.assertNotNull(AccountLinkActionEnum.fromValue((int) v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    public void shouldAccountRestrictionTypeEnumMapToAccountRestrictionType() {
        Arrays.stream(AccountRestrictionTypeEnum.values()).forEach(
            v -> Assertions.assertNotNull(AccountRestrictionType.rawValueOf(v.getValue())));
    }

    @Test
    public void shouldAccountRestrictionTypeMapToAccountRestrictionType() {
        Arrays.stream(AccountRestrictionType.values()).forEach(
            v -> Assertions
                .assertNotNull(AccountRestrictionTypeEnum.fromValue((int) v.getValue())));

    }

    @Test
    void testFromAccountLinkActionEnumToAccountLinkAction() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountLinkActionEnum.values()).forEach(v -> {
            Assertions.assertNotNull(AccountLinkAction.rawValueOf(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAliasActionModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AliasActionEnum.values()).forEach(v -> {
            Assertions.assertNotNull(AliasAction.rawValueOf(v.getValue().byteValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAliasActionCatbuffer() {
        Set<Byte> existingValues = new HashSet<>();
        Arrays.stream(AliasAction.values()).forEach(v -> {
            Assertions.assertNotNull(AliasActionDto.rawValueOf(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAliasActionDTO() {
        Set<Byte> existingValues = new HashSet<>();
        Arrays.stream(AliasAction.values()).forEach(v -> {
            Assertions.assertNotNull(AliasActionEnum.fromValue((int) v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testNamespaceRegistrationTypeDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(NamespaceRegistrationType.values()).forEach(v -> {
            Assertions
                .assertNotNull(NamespaceRegistrationTypeEnum.fromValue(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testNamespaceRegistrationTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(NamespaceRegistrationTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(NamespaceRegistrationType.rawValueOf(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testNamespaceRegistrationTypeCatbuffer() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(NamespaceRegistrationTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(NamespaceRegistrationTypeDto.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testTransactionTypeDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(TransactionType.values()).forEach(v -> {
            Assertions
                .assertNotNull(TransactionTypeEnum.fromValue(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testTransactionTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(TransactionTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(TransactionType.rawValueOf(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testAccountRestrictionModificationDTO() {
        Set<Byte> existingValues = new HashSet<>();
        Arrays.stream(AccountRestrictionModificationAction.values()).forEach(v -> {
            Assertions
                .assertNotNull(AccountRestrictionModificationActionEnum.fromValue(
                    (int) v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAccountRestrictionModificationTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountRestrictionModificationActionEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(
                    AccountRestrictionModificationAction.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testAccountTypeDto() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountType.values()).forEach(v -> {
            Assertions
                .assertNotNull(AccountTypeEnum.fromValue(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAccountTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(AccountType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAccountRestrictionTypeDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountRestrictionType.values()).forEach(v -> {
            Assertions
                .assertNotNull(AccountRestrictionTypeEnum.fromValue(v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testAccountRestrictionTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(AccountRestrictionTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(AccountRestrictionType.rawValueOf(v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testCosignatoryModificationActionDto() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(CosignatoryModificationActionType.values()).forEach(v -> {
            Assertions
                .assertNotNull(CosignatoryModificationActionEnum.fromValue((int) v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testCosignatoryModificationActionModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(CosignatoryModificationActionEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(
                    CosignatoryModificationActionType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testLockHashAlgorithmDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(LockHashAlgorithmType.values()).forEach(v -> {
            Assertions
                .assertNotNull(LockHashAlgorithmEnum.fromValue((int) v.getValue()), v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testLockHashAlgorithmTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(LockHashAlgorithmEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(LockHashAlgorithmType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testLockHashAlgorithmTypeCatbuffer() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(LockHashAlgorithmType.values()).forEach(v -> {
            Assertions
                .assertNotNull(LockHashAlgorithmDto.rawValueOf((byte) v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testMosaicRestrictionTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testMosaicRestrictionTypeDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionTypeDto.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testMosaicSupplyChangeActionEnumCatbuffer() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicSupplyChangeActionType.values()).forEach(v -> {
            Assertions.assertNotNull(MosaicSupplyChangeActionDto.rawValueOf((byte) v.getValue()),
                v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testMosaicSupplyChangeActionEnumDTO() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicSupplyChangeActionType.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicSupplyChangeActionEnum.fromValue(v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void MosaicSupplyChangeActionTypeModel() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicSupplyChangeActionEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicSupplyChangeActionType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testMosaicRestrictionTypeEnumToMosaicRestrictionType() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testMosaicRestrictionTypeToMosaicRestrictionTypeEnum() {
        Set<Byte> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionType.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionTypeEnum.fromValue((int) v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testMosaicRestrictionEntryTypeEnumToMosaicRestrictionEntryType() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionEntryTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionType.rawValueOf(v.getValue().byteValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testMosaicRestrictionEntryTypeToMosaicRestrictionEntryTypeEnum() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MosaicRestrictionEntryType.values()).forEach(v -> {
            Assertions
                .assertNotNull(MosaicRestrictionEntryTypeEnum.fromValue(v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


    @Test
    void testMessageTypeEnumToMessageType() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MessageTypeEnum.values()).forEach(v -> {
            Assertions
                .assertNotNull(MessageType.rawValueOf(v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }

    @Test
    void testMessageTypeToMessageTypeEnum() {
        Set<Integer> existingValues = new HashSet<>();
        Arrays.stream(MessageType.values()).forEach(v -> {
            Assertions
                .assertNotNull(MessageTypeEnum.fromValue(v.getValue()),
                    v.name());
            Assertions
                .assertTrue(existingValues.add(v.getValue()), v.getValue() + " is duplicated!!");
        });
    }


}
