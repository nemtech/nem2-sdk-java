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

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.metadata.MetadataEntry;
import io.nem.sdk.model.metadata.MetadataType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.openapi.vertx.api.MetadataRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.MetadataRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.MetadataDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MetadataEntriesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MetadataEntryDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * Implementation of {@link MetadataRepository}
 */
public class MetadataRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    MetadataRepository {

    private final MetadataRoutesApi client;

    public MetadataRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        client = new MetadataRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<List<Metadata>> getAccountMetadata(Address targetAddress,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getAccountMetadata(targetAddress.plain(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getAccountMetadataByKey(Address targetAddress, BigInteger key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getAccountMetadataByKey(targetAddress.plain(), toHex(key), handler);
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getMosaicMetadata(MosaicId targetMosaicId,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getMosaicMetadata(targetMosaicId.getIdAsHex(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId targetMosaicId, BigInteger key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getMosaicMetadataByKey(targetMosaicId.getIdAsHex(), toHex(key), handler);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getAccountMetadataByKeyAndSender(Address targetAddress, BigInteger key,
        String senderPublicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getAccountMetadataByKeyAndSender(targetAddress.plain(), toHex(key), senderPublicKey,
                handler);
        return handleOne(callback);
    }


    @Override
    public Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId targetMosaicId, BigInteger key,
        String senderPublicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getMosaicMetadataByKeyAndSender(targetMosaicId.getIdAsHex(), toHex(key),
                senderPublicKey,
                handler);
        return handleOne(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadata(NamespaceId targetNamespaceId,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getNamespaceMetadata(targetNamespaceId.getIdAsHex(), getPageSize(queryParams),
                getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId targetNamespaceId,
        BigInteger key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getNamespaceMetadataByKey(targetNamespaceId.getIdAsHex(), toHex(key), handler);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId targetNamespaceId,
        BigInteger key, String senderPublicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getNamespaceMetadataByKeyAndSender(targetNamespaceId.getIdAsHex(), toHex(key),
                senderPublicKey,
                handler);
        return handleOne(callback);
    }


    public MetadataRoutesApi getClient() {
        return client;
    }

    /**
     * It handles an async call result of a list of {@link MetadataEntriesDTO} converting it into a
     * {@link Observable} list of {@link Metadata}.
     *
     * @param callback the callback
     * @return the {@link Observable} list of {@link Metadata}.
     */
    private Observable<List<Metadata>> handleList(
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback) {
        return exceptionHandling(
            call(callback).map(MetadataEntriesDTO::getMetadataEntries).flatMapIterable(item -> item)
                .map(this::toMetadata).toList()
                .toObservable());
    }

    /**
     * It handles an async call result of a {@link MetadataEntriesDTO} converting it into a {@link
     * Observable} of {@link Metadata}.
     *
     * @param callback the callback
     * @return the {@link Observable} of {@link Metadata}.
     */
    private Observable<Metadata> handleOne(
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback) {
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
            entryDto.getSenderPublicKey(), entryDto.getTargetPublicKey(),
            new BigInteger(entryDto.getScopedMetadataKey(), 16),
            MetadataType.rawValueOf(entryDto.getMetadataType().getValue()),
            ConvertUtils.fromHexToString(entryDto.getValue()),
            Optional.ofNullable(Objects.toString(entryDto.getTargetId(), null)));
        return new Metadata(dto.getId(), metadataEntry);
    }


    protected String toHex(BigInteger key) {
        return ConvertUtils.toSize16Hex(key);
    }
}
