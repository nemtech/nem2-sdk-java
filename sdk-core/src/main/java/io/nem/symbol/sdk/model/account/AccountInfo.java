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

import io.nem.symbol.catapult.builders.AccountKeyTypeFlagsDto;
import io.nem.symbol.catapult.builders.AccountStateBuilder;
import io.nem.symbol.catapult.builders.AccountTypeDto;
import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.FinalizationEpochDto;
import io.nem.symbol.catapult.builders.HeightActivityBucketBuilder;
import io.nem.symbol.catapult.builders.HeightActivityBucketsBuilder;
import io.nem.symbol.catapult.builders.HeightDto;
import io.nem.symbol.catapult.builders.ImportanceDto;
import io.nem.symbol.catapult.builders.ImportanceHeightDto;
import io.nem.symbol.catapult.builders.ImportanceSnapshotBuilder;
import io.nem.symbol.catapult.builders.KeyDto;
import io.nem.symbol.catapult.builders.MosaicBuilder;
import io.nem.symbol.catapult.builders.PinnedVotingKeyBuilder;
import io.nem.symbol.catapult.builders.VotingKeyDto;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

/**
 * The account info structure describes basic information for an account.
 *
 * @since 1.0
 */
public class AccountInfo implements Stored {

  private final String recordId;
  private final int version;
  private final Address address;
  private final BigInteger addressHeight;
  private final PublicKey publicKey;
  private final BigInteger publicKeyHeight;
  private final Importance importance;
  private final List<ResolvedMosaic> mosaics;
  private final AccountType accountType;
  private final SupplementalAccountKeys supplementalAccountKeys;
  private final List<ActivityBucket> activityBuckets;

  @SuppressWarnings("squid:S00107")
  public AccountInfo(
      String recordId,
      int version,
      Address address,
      BigInteger addressHeight,
      PublicKey publicKey,
      BigInteger publicKeyHeight,
      BigInteger importance,
      BigInteger importanceHeight,
      List<ResolvedMosaic> mosaics,
      AccountType accountType,
      SupplementalAccountKeys supplementalAccountKeys,
      List<ActivityBucket> activityBuckets) {
    Validate.notNull(address, "address is required");
    Validate.notNull(addressHeight, "addressHeight is required");
    Validate.notNull(publicKey, "publicKey is required");
    Validate.notNull(publicKeyHeight, "publicKeyHeight is required");
    Validate.notNull(importance, "importance is required");
    Validate.notNull(importanceHeight, "importanceHeight is required");
    Validate.notNull(accountType, "accountType is required");
    Validate.notNull(supplementalAccountKeys, "supplementalAccountKeys is required");
    this.version = version;
    this.recordId = recordId;
    this.address = address;
    this.addressHeight = addressHeight;
    this.publicKey = publicKey;
    this.publicKeyHeight = publicKeyHeight;
    this.accountType = accountType;
    this.supplementalAccountKeys = supplementalAccountKeys;
    this.activityBuckets = ObjectUtils.defaultIfNull(activityBuckets, Collections.emptyList());
    this.importance = new Importance(importance, importanceHeight);
    this.mosaics = ObjectUtils.defaultIfNull(mosaics, Collections.emptyList());
    //    if (isHighValue()) {
    //      Validate.isTrue(
    //          this.activityBuckets.size() >= 5, "Activity buckets must contain at least 5
    // values");
    //    }
  }

  /** @return the record id if known. */
  @Override
  public Optional<String> getRecordId() {
    return Optional.ofNullable(this.recordId);
  }

  /**
   * Returns account address.
   *
   * @return {@link Address}
   */
  public Address getAddress() {
    return address;
  }

  /**
   * Returns height when the address was published.
   *
   * @return BigInteger
   */
  public BigInteger getAddressHeight() {
    return addressHeight;
  }

  /**
   * Returns public key of the account.
   *
   * @return String
   */
  public PublicKey getPublicKey() {
    return publicKey;
  }

  /**
   * Returns height when the public key was published.
   *
   * @return BigInteger
   */
  public BigInteger getPublicKeyHeight() {
    return publicKeyHeight;
  }

  /**
   * Returns the Importance of the account.
   *
   * @return BigInteger
   */
  public Importance getImportance() {
    return importance;
  }

  /**
   * Returns mosaics hold by the account.
   *
   * @return List of {@link ResolvedMosaic}
   */
  public List<ResolvedMosaic> getMosaics() {
    return mosaics;
  }

  /**
   * Returns height when the address was published.
   *
   * @return {@link PublicAccount}
   */
  public PublicAccount getPublicAccount() {
    return PublicAccount.createFromPublicKey(this.publicKey.toHex(), this.address.getNetworkType());
  }

  /** @return the account type. */
  public AccountType getAccountType() {
    return accountType;
  }

  /** @return the supplemental account keys. */
  public SupplementalAccountKeys getSupplementalAccountKeys() {
    return supplementalAccountKeys;
  }

  /** @return the activity buckets. */
  public List<ActivityBucket> getActivityBuckets() {
    return activityBuckets;
  }

  /**
   * If the account is a harvesting account.
   *
   * @return if it's high value.
   */
  public boolean isHighValue() {
    return getImportance().getValue().compareTo(BigInteger.ZERO) > 0;
  }

  /** @return the version */
  public int getVersion() {
    return version;
  }

  public byte[] serialize() {

    AddressDto address = SerializationUtils.toAddressDto(getAddress());
    HeightDto addressHeight = new HeightDto(getAddressHeight().longValue());
    KeyDto publicKey = SerializationUtils.toKeyDto(getPublicKey());
    HeightDto publicKeyHeight = new HeightDto(getPublicKeyHeight().longValue());
    AccountTypeDto accountType = AccountTypeDto.rawValueOf((byte) getAccountType().getValue());
    EnumSet<AccountKeyTypeFlagsDto> supplementalPublicKeysMask = getAccountKeyTypeFlags();
    KeyDto linkedPublicKey =
        getSupplementalAccountKeys().getLinked().map(SerializationUtils::toKeyDto).orElse(null);
    KeyDto nodePublicKey =
        getSupplementalAccountKeys().getNode().map(SerializationUtils::toKeyDto).orElse(null);
    KeyDto vrfPublicKey =
        getSupplementalAccountKeys().getVrf().map(SerializationUtils::toKeyDto).orElse(null);
    List<PinnedVotingKeyBuilder> votingPublicKeys =
        getSupplementalAccountKeys().getVoting().stream()
            .map(this::toPinnedVotingKeyBuilder)
            .collect(Collectors.toList());
    ImportanceDto importanceValue = new ImportanceDto(getImportance().getValue().longValue());
    ImportanceHeightDto importanceHeight =
        new ImportanceHeightDto(getImportance().getHeight().longValue());
    ImportanceSnapshotBuilder importanceSnapshots =
        ImportanceSnapshotBuilder.create(importanceValue, importanceHeight);
    HeightActivityBucketsBuilder activityBuckets = toHeightActivityBucketsBuilder();
    List<MosaicBuilder> balances = SerializationUtils.toMosaicBuilders(getMosaics());

    if (isHighValue()) {
      return AccountStateBuilder.createHighValue(
              (short) getVersion(),
              address,
              addressHeight,
              publicKey,
              publicKeyHeight,
              accountType,
              supplementalPublicKeysMask,
              linkedPublicKey,
              nodePublicKey,
              vrfPublicKey,
              votingPublicKeys,
              importanceSnapshots,
              activityBuckets,
              balances)
          .serialize();
    } else {
      return AccountStateBuilder.createRegular(
              (short) getVersion(),
              address,
              addressHeight,
              publicKey,
              publicKeyHeight,
              accountType,
              supplementalPublicKeysMask,
              linkedPublicKey,
              nodePublicKey,
              vrfPublicKey,
              votingPublicKeys,
              balances)
          .serialize();
    }
  }

  private HeightActivityBucketsBuilder toHeightActivityBucketsBuilder() {
    List<HeightActivityBucketBuilder> buckets =
        getActivityBuckets().stream()
            .limit(5)
            .map(this::toHeightActivityBucketBuilder)
            .collect(Collectors.toList());
    return HeightActivityBucketsBuilder.create(buckets);
  }

  private HeightActivityBucketBuilder toHeightActivityBucketBuilder(ActivityBucket activityBucket) {
    ImportanceHeightDto startHeight =
        new ImportanceHeightDto(activityBucket.getStartHeight().longValue());
    AmountDto totalFeesPaid = SerializationUtils.toAmount(activityBucket.getTotalFeesPaid());
    int beneficiaryCount = (int) activityBucket.getBeneficiaryCount();
    long rawScore = activityBucket.getRawScore().longValue();
    return HeightActivityBucketBuilder.create(
        startHeight, totalFeesPaid, beneficiaryCount, rawScore);
  }

  private PinnedVotingKeyBuilder toPinnedVotingKeyBuilder(
      AccountLinkVotingKey accountLinkVotingKey) {

    VotingKeyDto votingKey = SerializationUtils.toVotingKeyDto(accountLinkVotingKey.getPublicKey());
    FinalizationEpochDto startEpoch =
        new FinalizationEpochDto((int) accountLinkVotingKey.getStartEpoch());
    FinalizationEpochDto endEpoch =
        new FinalizationEpochDto((int) accountLinkVotingKey.getEndEpoch());
    return PinnedVotingKeyBuilder.create(votingKey, startEpoch, endEpoch);
  }

  /**
   * Get the mosaic flags.
   *
   * @return Mosaic flags
   */
  private EnumSet<AccountKeyTypeFlagsDto> getAccountKeyTypeFlags() {
    EnumSet<AccountKeyTypeFlagsDto> flags = EnumSet.of(AccountKeyTypeFlagsDto.UNSET);
    if (getSupplementalAccountKeys().getVrf().isPresent()) {
      flags.add(AccountKeyTypeFlagsDto.VRF);
    }
    if (getSupplementalAccountKeys().getNode().isPresent()) {
      flags.add(AccountKeyTypeFlagsDto.NODE);
    }
    if (getSupplementalAccountKeys().getLinked().isPresent()) {
      flags.add(AccountKeyTypeFlagsDto.LINKED);
    }
    return flags;
  }
}
