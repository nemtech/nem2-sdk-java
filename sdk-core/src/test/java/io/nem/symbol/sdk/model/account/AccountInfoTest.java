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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.catapult.builders.AccountStateBuilder;
import io.nem.symbol.catapult.builders.AccountStateFormatDto;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccountInfoTest {

  PublicKey accountPublicKey =
      PublicKey.fromHexString("1111111111111111111111111111111111111111111111111111111111111111");
  PublicKey linkedKey =
      PublicKey.fromHexString("2222222222222222222222222222222222222222222222222222222222222222");
  PublicKey nodeKey =
      PublicKey.fromHexString("3333333333333333333333333333333333333333333333333333333333333333");
  PublicKey vrfKey =
      PublicKey.fromHexString("4444444444444444444444444444444444444444444444444444444444444444");
  String votingKey = "5555555555555555555555555555555555555555555555555555555555555555";
  NetworkType networkType = NetworkType.MIJIN_TEST;
  Address address = Address.createFromPublicKey(accountPublicKey.toHex(), networkType);

  @Test
  void highValueAccountCreationAndSerialization() {
    List<ResolvedMosaic> mosaics =
        Collections.singletonList(
            new ResolvedMosaic(new MosaicId(BigInteger.TEN), BigInteger.valueOf(10000)));

    SupplementalAccountKeys supplementalAccountKeys =
        new SupplementalAccountKeys(
            linkedKey,
            nodeKey,
            vrfKey,
            Collections.singletonList(new AccountLinkVotingKey(votingKey, 10, 20)));

    List<ActivityBucket> activityBuckets =
        Stream.of(1, 2, 3, 4, 5)
            .map(
                index ->
                    new ActivityBucket(
                        BigInteger.valueOf(index),
                        BigInteger.valueOf(10 * index),
                        100 * index,
                        BigInteger.valueOf(1000 * index)))
            .collect(Collectors.toList());

    AccountInfo accountInfo =
        new AccountInfo(
            "abc",
            1,
            address,
            new BigInteger("964"),
            accountPublicKey,
            new BigInteger("966"),
            new BigInteger("777"),
            new BigInteger("0"),
            mosaics,
            AccountType.REMOTE_UNLINKED,
            supplementalAccountKeys,
            activityBuckets);

    assertEquals("abc", accountInfo.getRecordId().get());
    assertEquals(address, accountInfo.getAddress());
    assertEquals(new BigInteger("964"), accountInfo.getAddressHeight());
    assertEquals(accountPublicKey, accountInfo.getPublicKey());
    assertEquals(new BigInteger("966"), accountInfo.getPublicKeyHeight());
    assertEquals(new BigInteger("777"), accountInfo.getImportance().getValue());
    assertEquals(new BigInteger("0"), accountInfo.getImportance().getHeight());
    assertEquals(mosaics, accountInfo.getMosaics());
    assertEquals(
        PublicAccount.createFromPublicKey(accountPublicKey.toHex(), NetworkType.MIJIN_TEST),
        accountInfo.getPublicAccount());

    assertEquals(AccountType.REMOTE_UNLINKED, accountInfo.getAccountType());
    assertTrue(accountInfo.isHighValue());

    assertEquals(supplementalAccountKeys, accountInfo.getSupplementalAccountKeys());
    assertEquals(activityBuckets, accountInfo.getActivityBuckets());

    assertEquals(linkedKey, supplementalAccountKeys.getLinked().get());
    assertEquals(nodeKey, supplementalAccountKeys.getNode().get());
    assertEquals(vrfKey, supplementalAccountKeys.getVrf().get());
    assertEquals(votingKey, supplementalAccountKeys.getVoting().get(0).getPublicKey());
    assertEquals(10, supplementalAccountKeys.getVoting().get(0).getStartEpoch());
    assertEquals(20, supplementalAccountKeys.getVoting().get(0).getEndEpoch());

    byte[] serializedState = accountInfo.serialize();
    assertEquals(388, serializedState.length);
    String expectedHex =
        "010090FD35818960C7B18B72F49A5598FA9F712A354DB38EB076C4030000000000001111111111111111111111111111111111111111111111111111111111111111C6030000000000000301070122222222222222222222222222222222222222222222222222222222222222223333333333333333333333333333333333333333333333333333333333333333444444444444444444444444444444444444444444444444444444444444444455555555555555555555555555555555555555555555555555555555555555550A000000140000000903000000000000000000000000000001000000000000000A0000000000000064000000E80300000000000002000000000000001400000000000000C8000000D00700000000000003000000000000001E000000000000002C010000B80B0000000000000400000000000000280000000000000090010000A00F00000000000005000000000000003200000000000000F4010000881300000000000001000A000000000000001027000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountStateBuilder accountStateBuilder =
        AccountStateBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(AccountStateFormatDto.HIGH_VALUE, accountStateBuilder.getFormat());

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(accountStateBuilder.serialize()));
  }

  @Test
  void regularValueAccountCreationAndSerialization() {
    List<ResolvedMosaic> mosaics =
        Collections.singletonList(
            new ResolvedMosaic(new MosaicId(BigInteger.TEN), BigInteger.valueOf(10000)));

    SupplementalAccountKeys supplementalAccountKeys =
        new SupplementalAccountKeys(
            linkedKey,
            nodeKey,
            vrfKey,
            Collections.singletonList(new AccountLinkVotingKey(votingKey, 10, 20)));

    List<ActivityBucket> activityBuckets = Collections.emptyList();
    AccountInfo accountInfo =
        new AccountInfo(
            "abc",
            1,
            address,
            new BigInteger("964"),
            accountPublicKey,
            new BigInteger("966"),
            BigInteger.ZERO,
            BigInteger.ZERO,
            mosaics,
            AccountType.REMOTE_UNLINKED,
            supplementalAccountKeys,
            activityBuckets);

    assertEquals("abc", accountInfo.getRecordId().get());
    assertEquals(address, accountInfo.getAddress());
    assertEquals(new BigInteger("964"), accountInfo.getAddressHeight());
    assertEquals(accountPublicKey, accountInfo.getPublicKey());
    assertEquals(new BigInteger("966"), accountInfo.getPublicKeyHeight());
    assertEquals(new BigInteger("0"), accountInfo.getImportance().getValue());
    assertEquals(new BigInteger("0"), accountInfo.getImportance().getHeight());
    assertEquals(mosaics, accountInfo.getMosaics());
    assertEquals(
        PublicAccount.createFromPublicKey(accountPublicKey.toHex(), networkType),
        accountInfo.getPublicAccount());

    assertEquals(AccountType.REMOTE_UNLINKED, accountInfo.getAccountType());

    assertEquals(supplementalAccountKeys, accountInfo.getSupplementalAccountKeys());
    assertEquals(activityBuckets, accountInfo.getActivityBuckets());

    assertEquals(linkedKey, supplementalAccountKeys.getLinked().get());
    assertEquals(nodeKey, supplementalAccountKeys.getNode().get());
    assertEquals(vrfKey, supplementalAccountKeys.getVrf().get());
    assertEquals(votingKey, supplementalAccountKeys.getVoting().get(0).getPublicKey());
    assertEquals(10, supplementalAccountKeys.getVoting().get(0).getStartEpoch());
    assertEquals(20, supplementalAccountKeys.getVoting().get(0).getEndEpoch());
    assertFalse(accountInfo.isHighValue());

    byte[] serializedState = accountInfo.serialize();
    String expectedHex =
        "010090FD35818960C7B18B72F49A5598FA9F712A354DB38EB076C4030000000000001111111111111111111111111111111111111111111111111111111111111111C6030000000000000300070122222222222222222222222222222222222222222222222222222222222222223333333333333333333333333333333333333333333333333333333333333333444444444444444444444444444444444444444444444444444444444444444455555555555555555555555555555555555555555555555555555555555555550A0000001400000001000A000000000000001027000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountStateBuilder builder =
        AccountStateBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(AccountStateFormatDto.REGULAR, builder.getFormat());

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
