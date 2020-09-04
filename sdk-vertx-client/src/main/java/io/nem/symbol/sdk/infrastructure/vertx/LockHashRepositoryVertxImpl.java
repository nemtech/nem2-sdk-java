package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.LockHashRepository;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.openapi.vertx.api.LockHashRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.LockHashRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockEntryDTO;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockPage;
import io.nem.symbol.sdk.openapi.vertx.model.Order;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LockHashRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements LockHashRepository {

    private final LockHashRoutesApi client;

    public LockHashRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new LockHashRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<HashLockInfo> getLockHash(String hash) {
        return this.call((h) -> getClient().getLockHash(hash, h), this::toHashLockInfo);

    }

    private HashLockInfo toHashLockInfo(HashLockInfoDTO dto) {
        HashLockEntryDTO lock = dto.getLock();
        return new HashLockInfo(Optional.of(dto.getId()), MapperUtils.toAddress(lock.getOwnerAddress()),
            MapperUtils.toMosaicId(lock.getMosaicId()), lock.getAmount(), lock.getEndHeight(), lock.getStatus(),
            lock.getHash());
    }

    @Override
    public Observable<Page<HashLockInfo>> search(HashLockSearchCriteria criteria) {
        String address = toDto(criteria.getAddress());
        Integer pageSize = criteria.getPageSize();
        Integer pageNumber = criteria.getPageNumber();
        String offset = criteria.getOffset();
        Order order = toDto(criteria.getOrder());
        Consumer<Handler<AsyncResult<HashLockPage>>> handlerConsumer = (h) -> getClient()
            .searchLockHash(address, pageSize, pageNumber, offset, order, h);
        return this.call(handlerConsumer, this::toPage);
    }

    private Page<HashLockInfo> toPage(HashLockPage hashLockPage) {
        return toPage(hashLockPage.getPagination(),
            hashLockPage.getData().stream().map(this::toHashLockInfo).collect(Collectors.toList()));
    }

    public LockHashRoutesApi getClient() {
        return client;
    }

}
