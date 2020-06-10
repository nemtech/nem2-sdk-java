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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.QueryParams;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataEntry;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.MetadataRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataEntriesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataEntryDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;


/**
 * Implementation of {@link MetadataRepository}
 */
public class MetadataRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    MetadataRepository {

    private final MetadataRoutesApi client;

    public MetadataRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new MetadataRoutesApi(apiClient);
    }

    @Override
    public Observable<List<Metadata>> getAccountMetadata(Address targetAddress,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getAccountMetadata(targetAddress.plain(), getPageSize(queryParams),
                getOrder(queryParams),
                getId(queryParams)
            );
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getAccountMetadataByKey(Address targetAddress, BigInteger key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getAccountMetadataByKey(targetAddress.plain(), toHex(key));
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getMosaicMetadata(MosaicId targetMosaicId,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getMosaicMetadata(targetMosaicId.getIdAsHex(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams));
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId targetMosaicId, BigInteger key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getMosaicMetadataByKey(targetMosaicId.getIdAsHex(), toHex(key));
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getAccountMetadataByKeyAndSender(Address targetAddress, BigInteger key,
        Address sourceAddress) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getAccountMetadataByKeyAndSender(targetAddress.plain(), toHex(key), sourceAddress.plain());
        return handleOne(callback);
    }


    @Override
    public Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId targetMosaicId, BigInteger key,
        Address sourceAddress) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getMosaicMetadataByKeyAndSender(targetMosaicId.getIdAsHex(), toHex(key),
                sourceAddress.plain());
        return handleOne(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadata(NamespaceId targetNamespaceId,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getNamespaceMetadata(targetNamespaceId.getIdAsHex(), getPageSize(queryParams),
                getId(queryParams),
                getOrder(queryParams));
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId targetNamespaceId,
        BigInteger key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getNamespaceMetadataByKey(targetNamespaceId.getIdAsHex(), toHex(key));
        return handleList(callback);
    }


    @Override
    public Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId targetNamespaceId,
        BigInteger key, Address sourceAddress) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getNamespaceMetadataByKeyAndSender(targetNamespaceId.getIdAsHex(),
                MetadataRepositoryOkHttpImpl.this.toHex(key), sourceAddress.plain());
        return handleOne(callback);
    }

    public MetadataRoutesApi getClient() {
        return client;
    }

    /**
     * It handles the callback that returns a list of {@link MetadataEntriesDTO} converting it into a {@link Observable}
     * list of {@link Metadata}.
     *
     * @param callback the callback
     * @return the {@link Observable} list of {@link Metadata}.
     */
    private Observable<List<Metadata>> handleList(
        Callable<MetadataEntriesDTO> callback) {
        return exceptionHandling(
            call(callback).map(MetadataEntriesDTO::getMetadataEntries).flatMapIterable(item -> item)
                .map(this::toMetadata).toList()
                .toObservable());
    }

    /**
     * It handles the callback that returns one {@link MetadataEntriesDTO} converting it into a {@link Observable} of
     * {@link Metadata}.
     *
     * @param callback the callback
     * @return the {@link Observable} of {@link Metadata}.
     */
    private Observable<Metadata> handleOne(
        Callable<MetadataDTO> callback) {
        return exceptionHandling(call(callback)
            .map(this::toMetadata));
    }

    /**
     * It converts the {@link MetadataDTO} into a model {@link Metadata}.
     *
     * @param dto the {@link MetadataDTO}
     * @return the {@link Metadata}
     */
    private Metadata toMetadata(MetadataDTO dto) {
        MetadataEntryDTO entryDto = dto.getMetadataEntry();
        MetadataEntry metadataEntry = new MetadataEntry(entryDto.getCompositeHash(),
            MapperUtils.toAddress(entryDto.getSourceAddress()),
            MapperUtils.toAddress(entryDto.getTargetAddress()),
            new BigInteger(entryDto.getScopedMetadataKey(), 16),
            MetadataType.rawValueOf(entryDto.getMetadataType().getValue()),
            ConvertUtils.fromHexToString(entryDto.getValue()),
            Optional.ofNullable(Objects.toString(entryDto.getTargetId(), null)));
        return new Metadata(dto.getId(), metadataEntry);
    }

    private String toHex(BigInteger key) {
        return ConvertUtils.toSize16Hex(key);
    }

}
