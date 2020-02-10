/*
 *  Copyright 2018 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.api;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.reactivex.Observable;
import java.io.Closeable;

/**
 * A repository factory allows clients to create repositories to access NEM Server without knowing
 * the underline implementation.
 *
 * @author Fernando Boucquez
 */
public interface RepositoryFactory extends Closeable {


    /**
     * @return a newly created {@link AccountRepository}
     */
    AccountRepository createAccountRepository();

    /**
     * @return a newly created {@link MultisigRepository}
     */
    MultisigRepository createMultisigRepository();

    /**
     * @return a newly created {@link BlockRepository}
     */
    BlockRepository createBlockRepository();

    /**
     * @return a newly created {@link ReceiptRepository}
     */
    ReceiptRepository createReceiptRepository();

    /**
     * @return a newly created {@link ChainRepository}
     */
    ChainRepository createChainRepository();

    /**
     * @return a newly created {@link MosaicRepository}
     */
    MosaicRepository createMosaicRepository();

    /**
     * @return a newly created {@link NamespaceRepository}
     */
    NamespaceRepository createNamespaceRepository();

    /**
     * @return a newly created {@link NetworkRepository}
     */
    NetworkRepository createNetworkRepository();

    /**
     * @return a newly created {@link NodeRepository}
     */
    NodeRepository createNodeRepository();

    /**
     * @return a newly created {@link NodeRepository}
     */
    TransactionRepository createTransactionRepository();

    /**
     * @return a newly created {@link MetadataRepository}
     */
    MetadataRepository createMetadataRepository();

    /**
     * @return a newly created {@link RestrictionAccountRepository}
     */
    RestrictionAccountRepository createRestrictionAccountRepository();

    /**
     * @return a newly created {@link RestrictionMosaicRepository}
     */
    RestrictionMosaicRepository createRestrictionMosaicRepository();

    /**
     * @return a newly created {@link Listener}
     */
    Listener createListener();

    /**
     * @return it creates a new {@link JsonSerialization} that allows you serialize model objects
     * using the generated json dto objects from the open api spec.
     */
    JsonSerialization createJsonSerialization();

    /**
     * It closes the underling connection if necessary.
     */
    void close();

    /**
     * @return the network type of the network. It uses the user configured network type if
     * provided, if not, the value will be resolved from the server. This method is cached, the
     * server is only called the first time if necessary.
     * @see RepositoryFactoryConfiguration
     */
    Observable<NetworkType> getNetworkType();

    /**
     * @return the generation hash used to sign transactions.  It uses the user configured
     * generation hash if provided, if not, the value will be resolved from the server. Value
     * retrieved from the block/1 endpoint. This method is cached, the server is only called the
     * first time.
     * @see RepositoryFactoryConfiguration
     */
    Observable<String> getGenerationHash();

    /**
     * @return the configured network currency configuration like "cat.harvest", "nem.xem " and
     * "symbol.xym". This method uses the user configured properties if provided. If it's not
     * provided, it resolves the configuration by querying block 1 transactions. This method is
     * cached, the server is only called the first time.
     * @see NetworkCurrencyService
     * @see RepositoryFactoryConfiguration
     */
    Observable<NetworkCurrency> getNetworkCurrency();

    /**
     * @return the configured harvest currency configuration like "cat.harvest". This method uses
     * the user configured properties if provided. If it's not provided, it resolves the
     * configuration by querying block 1 transactions. This method is cached, the server is only
     * called the first time. The network currency configuration
     * @see NetworkCurrencyService
     * @see RepositoryFactoryConfiguration
     */
    Observable<NetworkCurrency> getHarvestCurrency();
}
