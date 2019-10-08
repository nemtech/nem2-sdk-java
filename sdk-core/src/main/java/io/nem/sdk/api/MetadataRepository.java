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

package io.nem.sdk.api;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Metadata interface repository.
 */
public interface MetadataRepository {

    /**
     * Returns the account metadata given an account id.
     *
     * @param address - Account address to be created from PublicKey or RawAddress
     * @param queryParams - Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getAccountMetadata(Address address,
        Optional<QueryParams> queryParams);

    /**
     * Returns the account metadata given an account id and a key
     *
     * @param address - Account address to be created from PublicKey or RawAddress
     * @param key - Metadata key
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getAccountMetadataByKey(Address address, BigInteger key);

    /**
     * Returns the account metadata given an account id and a key
     *
     * @param address - Account address to be created from PublicKey or RawAddress
     * @param key - Metadata key
     * @param publicKey - Sender public key
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getAccountMetadataByKeyAndSender(Address address, BigInteger key,
        String publicKey);

    /**
     * Returns the mosaic metadata given a mosaic id.
     *
     * @param mosaicId - Mosaic identifier.
     * @param queryParams - Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getMosaicMetadata(MosaicId mosaicId,
        Optional<QueryParams> queryParams);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param mosaicId - Mosaic identifier.
     * @param key - Metadata key.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId mosaicId, BigInteger key);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param mosaicId - Mosaic identifier.
     * @param key - Metadata key.
     * @param publicKey - Sender public key
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId mosaicId, BigInteger key,
        String publicKey);

    /**
     * Returns the mosaic metadata given a mosaic id.
     *
     * @param namespaceId - Namespace identifier.
     * @param queryParams - Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getNamespaceMetadata(NamespaceId namespaceId,
        Optional<QueryParams> queryParams);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param namespaceId - Namespace identifier.
     * @param key - Metadata key.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId namespaceId, BigInteger key);

    /**
     * Returns the namespace metadata given a mosaic id and metadata key.
     *
     * @param namespaceId - Namespace identifier.
     * @param key - Metadata key.
     * @param publicKey - Sender public key
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId namespaceId,
        BigInteger key, String publicKey);

}
