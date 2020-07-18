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

package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;

/**
 * Metadata interface repository.
 */
public interface MetadataRepository extends Searcher<Metadata, MetadataSearchCriteria> {

    /**
     * Returns the account metadata given an account id and a key
     *
     * @param targetAddress the address that holds the medata values with the given key sent by the given public key.
     * @param key - Metadata key
     * @param sourceAddress The address of the account that created the metadata.
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getAccountMetadataByKeyAndSender(Address targetAddress, BigInteger key, Address sourceAddress);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param targetMosaicId The mosaic id that holds the metadata values.
     * @param key Metadata key.
     * @param sourceAddress The address of the account that created the metadata.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId targetMosaicId, BigInteger key,
        Address sourceAddress);

    /**
     * Returns the namespace metadata given a mosaic id and metadata key.
     *
     * @param targetNamespaceId The namespace id that holds the metadata values.
     * @param key Metadata key.
     * @param sourceAddress The address of the account that created the metadata.
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId targetNamespaceId, BigInteger key,
        Address sourceAddress);

}
