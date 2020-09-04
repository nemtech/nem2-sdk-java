package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.LockHashRepository;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.LockHashRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.reactivex.Observable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implements {@link LockHashRepository}
 *
 * @author Fernando Boucquez
 */
public class LockHashRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements LockHashRepository {

    private final LockHashRoutesApi client;

    public LockHashRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new LockHashRoutesApi(apiClient);
    }

    @Override
    public Observable<HashLockInfo> getLockHash(String hash) {
        Callable<HashLockInfoDTO> callback = () -> getClient().getLockHash(hash);
        return this.call(callback, this::toHashLockInfo);

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
        Callable<HashLockPage> callback = () -> getClient()
            .searchLockHash(address, pageSize, pageNumber, offset, order);
        return this.call(callback, this::toPage);
    }

    private Page<HashLockInfo> toPage(HashLockPage hashLockPage) {
        return toPage(hashLockPage.getPagination(),
            hashLockPage.getData().stream().map(this::toHashLockInfo).collect(Collectors.toList()));
    }

    public LockHashRoutesApi getClient() {
        return client;
    }
}
