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

package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.model.network.AccountLinkNetworkProperties;
import io.nem.symbol.sdk.model.network.AccountRestrictionNetworkProperties;
import io.nem.symbol.sdk.model.network.AggregateNetworkProperties;
import io.nem.symbol.sdk.model.network.ChainProperties;
import io.nem.symbol.sdk.model.network.HashLockNetworkProperties;
import io.nem.symbol.sdk.model.network.MetadataNetworkProperties;
import io.nem.symbol.sdk.model.network.MosaicNetworkProperties;
import io.nem.symbol.sdk.model.network.MosaicRestrictionNetworkProperties;
import io.nem.symbol.sdk.model.network.MultisigNetworkProperties;
import io.nem.symbol.sdk.model.network.NamespaceNetworkProperties;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkInfo;
import io.nem.symbol.sdk.model.network.NetworkProperties;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.network.NodeIdentityEqualityStrategy;
import io.nem.symbol.sdk.model.network.PluginsProperties;
import io.nem.symbol.sdk.model.network.RentalFees;
import io.nem.symbol.sdk.model.network.SecretLockNetworkProperties;
import io.nem.symbol.sdk.model.network.TransactionFees;
import io.nem.symbol.sdk.model.network.TransferNetworkProperties;
import io.nem.symbol.sdk.openapi.vertx.api.NetworkRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.NetworkRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.api.NodeRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.NodeRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AccountKeyLinkNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AggregateNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ChainPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MetadataNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicRestrictionNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NetworkTypeDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.PluginsPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.RentalFeesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockNetworkPropertiesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionFeesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransferNetworkPropertiesDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.function.Consumer;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    NetworkRepository {

    private final NetworkRoutesApi networkRoutesApi;
    private final NodeRoutesApi nodeRoutesApi;

    public NetworkRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        networkRoutesApi = new NetworkRoutesApiImpl(apiClient);
        nodeRoutesApi = new NodeRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<NetworkType> getNetworkType() {
        Consumer<Handler<AsyncResult<NodeInfoDTO>>> callback = handler -> getNodeRoutesApi()
            .getNodeInfo(handler);
        return exceptionHandling(
            call(callback).map(info -> NetworkType.rawValueOf(info.getNetworkIdentifier())));
    }

    @Override
    public Observable<TransactionFees> getTransactionFees() {
        Consumer<Handler<AsyncResult<TransactionFeesDTO>>> callback = handler -> getNetworkRoutesApi()
            .getTransactionFees(handler);
        return exceptionHandling(
            call(callback).map(info -> new TransactionFees(info.getAverageFeeMultiplier(),
                info.getMedianFeeMultiplier(), info.getLowestFeeMultiplier(),
                info.getHighestFeeMultiplier()
            )));
    }

    @Override
    public Observable<RentalFees> getRentalFees() {
        Consumer<Handler<AsyncResult<RentalFeesDTO>>> callback = handler -> getNetworkRoutesApi()
            .getRentalFees(handler);
        return exceptionHandling(
            call(callback)
                .map(info -> new RentalFees(info.getEffectiveRootNamespaceRentalFeePerBlock(),
                    info.getEffectiveChildNamespaceRentalFee(), info.getEffectiveMosaicRentalFee()
                )));
    }

    @Override
    public Observable<NetworkInfo> getNetworkInfo() {
        Consumer<Handler<AsyncResult<NetworkTypeDTO>>> callback = handler -> getNetworkRoutesApi()
            .getNetworkType(handler);
        return exceptionHandling(
            call(callback)
                .map(info -> new NetworkInfo(info.getName(), info.getDescription())));
    }


    @Override
    public Observable<NetworkConfiguration> getNetworkProperties() {
        return call(getNetworkRoutesApi()::getNetworkProperties)
            .map(info -> new NetworkConfiguration(toNetworkProperties(info.getNetwork()),
                toChainProperties(info.getChain()), toPluginsProperties(info.getPlugins())));
    }

    private NetworkProperties toNetworkProperties(NetworkPropertiesDTO dto) {
        return new NetworkProperties(dto.getIdentifier(),
            NodeIdentityEqualityStrategy.rawValueOf(dto.getNodeEqualityStrategy().getValue()),
            dto.getNemesisSignerPublicKey(),
            dto.getGenerationHashSeed(), dto.getEpochAdjustment());
    }

    private ChainProperties toChainProperties(ChainPropertiesDTO dto) {
        return new ChainProperties(dto.getEnableVerifiableState(), dto.getEnableVerifiableReceipts(),
            dto.getCurrencyMosaicId(), dto.getHarvestingMosaicId(), dto.getBlockGenerationTargetTime(),
            dto.getBlockTimeSmoothingFactor(), dto.getBlockFinalizationInterval(), dto.getImportanceGrouping(),
            dto.getImportanceActivityPercentage(), dto.getMaxRollbackBlocks(), dto.getMaxDifficultyBlocks(),
            dto.getDefaultDynamicFeeMultiplier(), dto.getMaxTransactionLifetime(), dto.getMaxBlockFutureTime(),
            dto.getInitialCurrencyAtomicUnits(), dto.getMaxMosaicAtomicUnits(), dto.getTotalChainImportance(),
            dto.getMinHarvesterBalance(), dto.getMaxHarvesterBalance(), dto.getMinVoterBalance(),
            dto.getMaxVotingKeysPerAccount(), dto.getMinVotingKeyLifetime(), dto.getMaxVotingKeyLifetime(),
            dto.getHarvestBeneficiaryPercentage(), dto.getHarvestNetworkPercentage(),
            dto.getHarvestNetworkFeeSinkAddress(), dto.getBlockPruneInterval(), dto.getMaxTransactionsPerBlock());
    }

    private PluginsProperties toPluginsProperties(PluginsPropertiesDTO dto) {
        return new PluginsProperties(
            toAccountlink(dto.getAccountlink()),
            toAggregate(dto.getAggregate()),
            toLockhash(dto.getLockhash()),
            toLocksecret(dto.getLocksecret()),
            toMetadata(dto.getMetadata()),
            toMosaic(dto.getMosaic()),
            toMultisig(dto.getMultisig()),
            toNamespace(dto.getNamespace()),
            toRestrictionaccount(dto.getRestrictionaccount()),
            toRestrictionmosaic(dto.getRestrictionmosaic()),
            toTransfer(dto.getTransfer()));
    }

    private AccountLinkNetworkProperties toAccountlink(AccountKeyLinkNetworkPropertiesDTO dto) {
        return new AccountLinkNetworkProperties(dto.getDummy());
    }

    private AggregateNetworkProperties toAggregate(AggregateNetworkPropertiesDTO dto) {
        return new AggregateNetworkProperties(dto.getMaxTransactionsPerAggregate(),
            dto.getMaxCosignaturesPerAggregate(), dto.getEnableStrictCosignatureCheck(),
            dto.getEnableBondedAggregateSupport(), dto.getMaxBondedTransactionLifetime());
    }

    private HashLockNetworkProperties toLockhash(HashLockNetworkPropertiesDTO dto) {
        return new HashLockNetworkProperties(dto.getLockedFundsPerAggregate(),
            dto.getMaxHashLockDuration());
    }

    private SecretLockNetworkProperties toLocksecret(SecretLockNetworkPropertiesDTO dto) {
        return new SecretLockNetworkProperties(dto.getMaxSecretLockDuration(),
            dto.getMinProofSize(), dto.getMaxProofSize());
    }

    private MetadataNetworkProperties toMetadata(MetadataNetworkPropertiesDTO dto) {
        return new MetadataNetworkProperties(dto.getMaxValueSize());
    }

    private MosaicNetworkProperties toMosaic(MosaicNetworkPropertiesDTO dto) {
        return new MosaicNetworkProperties(dto.getMaxMosaicsPerAccount(),
            dto.getMaxMosaicDuration(), dto.getMaxMosaicDivisibility(),
            dto.getMosaicRentalFeeSinkAddress(), dto.getMosaicRentalFee());
    }

    private MultisigNetworkProperties toMultisig(MultisigNetworkPropertiesDTO dto) {
        return new MultisigNetworkProperties(dto.getMaxMultisigDepth(),
            dto.getMaxCosignatoriesPerAccount(), dto.getMaxCosignedAccountsPerAccount());
    }

    private NamespaceNetworkProperties toNamespace(NamespaceNetworkPropertiesDTO dto) {
        return new NamespaceNetworkProperties(dto.getMaxNameSize(), dto.getMaxChildNamespaces(),
            dto.getMaxNamespaceDepth(), dto.getMinNamespaceDuration(),
            dto.getMaxNamespaceDuration(), dto.getNamespaceGracePeriodDuration(),
            dto.getReservedRootNamespaceNames(), dto.getNamespaceRentalFeeSinkAddress(),
            dto.getRootNamespaceRentalFeePerBlock(), dto.getChildNamespaceRentalFee());
    }

    private AccountRestrictionNetworkProperties toRestrictionaccount(
        AccountRestrictionNetworkPropertiesDTO dto) {
        return new AccountRestrictionNetworkProperties(dto.getMaxAccountRestrictionValues());
    }

    private MosaicRestrictionNetworkProperties toRestrictionmosaic(
        MosaicRestrictionNetworkPropertiesDTO dto) {
        return new MosaicRestrictionNetworkProperties(dto.getMaxMosaicRestrictionValues());
    }

    private TransferNetworkProperties toTransfer(TransferNetworkPropertiesDTO dto) {
        return new TransferNetworkProperties(dto.getMaxMessageSize());
    }


    public NetworkRoutesApi getNetworkRoutesApi() {
        return networkRoutesApi;
    }

    public NodeRoutesApi getNodeRoutesApi() {
        return nodeRoutesApi;
    }
}
