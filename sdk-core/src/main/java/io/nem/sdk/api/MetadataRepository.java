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
     * @param targetAddress the address that holds the medata values.
     * @param queryParams Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getAccountMetadata(Address targetAddress,
        Optional<QueryParams> queryParams);

    /**
     * Returns the account metadata given an account id and a key
     *
     * @param targetAddress the address that holds the medata values with the given key.
     * @param key Metadata key
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getAccountMetadataByKey(Address targetAddress, BigInteger key);

    /**
     * Returns the account metadata given an account id and a key
     *
     * @param targetAddress the address that holds the medata values with the given key sent by the
     * given public key.
     * @param key - Metadata key
     * @param senderPublicKey The public key of the account that created the metadata.
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getAccountMetadataByKeyAndSender(Address targetAddress, BigInteger key,
        String senderPublicKey);

    /**
     * Returns the mosaic metadata given a mosaic id.
     *
     * @param targetMosaicId The mosaic id that holds the metadata values.
     * @param queryParams Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getMosaicMetadata(MosaicId targetMosaicId,
        Optional<QueryParams> queryParams);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param targetMosaicId The mosaic id that holds the metadata values.
     * @param key Metadata key.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId targetMosaicId, BigInteger key);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param targetMosaicId The mosaic id that holds the metadata values.
     * @param key Metadata key.
     * @param senderPublicKey The public key of the account that created the metadata.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId targetMosaicId, BigInteger key,
        String senderPublicKey);

    /**
     * Returns the mosaic metadata given a mosaic id.
     *
     * @param targetNamespaceId The namespace id that holds the metadata values.
     * @param queryParams Optional query parameters
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getNamespaceMetadata(NamespaceId targetNamespaceId,
        Optional<QueryParams> queryParams);

    /**
     * Returns the mosaic metadata given a mosaic id and metadata key.
     *
     * @param targetNamespaceId The namespace id that holds the metadata values.
     * @param key Metadata key.
     * @return Observable of {@link Metadata} {@link List}
     */
    Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId targetNamespaceId,
        BigInteger key);

    /**
     * Returns the namespace metadata given a mosaic id and metadata key.
     *
     * @param targetNamespaceId The namespace id that holds the metadata values.
     * @param key Metadata key.
     * @param senderPublicKey The public key of the account that created the metadata.
     * @return Observable of {@link Metadata}
     */
    Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId targetNamespaceId,
        BigInteger key, String senderPublicKey);

}
