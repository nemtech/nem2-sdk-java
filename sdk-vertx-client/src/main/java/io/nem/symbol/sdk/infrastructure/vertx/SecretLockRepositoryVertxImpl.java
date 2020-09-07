package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.SecretHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.openapi.vertx.api.SecretLockRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.SecretLockRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.Order;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockEntryDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockPage;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SecretLockRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements SecretLockRepository {

    private final SecretLockRoutesApi client;

    public SecretLockRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new SecretLockRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<SecretLockInfo> getSecretLock(String secret) {
        return this.call((h) -> getClient().getSecretLock(secret, h), this::toSecretLockInfo);

    }

    private SecretLockInfo toSecretLockInfo(SecretLockInfoDTO dto) {
        SecretLockEntryDTO lock = dto.getLock();
        MosaicId mosaicId = MapperUtils.toMosaicId(lock.getMosaicId());
        return new SecretLockInfo(Optional.of(dto.getId()), MapperUtils.toAddress(lock.getOwnerAddress()), mosaicId,
            lock.getAmount(), lock.getEndHeight(), lock.getStatus(),
            SecretHashAlgorithm.rawValueOf(lock.getHashAlgorithm().getValue()), lock.getSecret(),
            MapperUtils.toAddress(lock.getRecipientAddress()), lock.getCompositeHash());
    }

    @Override
    public Observable<Page<SecretLockInfo>> search(SecretLockSearchCriteria criteria) {
        String address = toDto(criteria.getAddress());
        Integer pageSize = criteria.getPageSize();
        Integer pageNumber = criteria.getPageNumber();
        String offset = criteria.getOffset();
        Order order = toDto(criteria.getOrder());
        Consumer<Handler<AsyncResult<SecretLockPage>>> handlerConsumer = (h) -> getClient()
            .searchSecretLock(address, pageSize, pageNumber, offset, order, h);
        return this.call(handlerConsumer, this::toPage);
    }

    private Page<SecretLockInfo> toPage(SecretLockPage SecretLockPage) {
        return toPage(SecretLockPage.getPagination(),
            SecretLockPage.getData().stream().map(this::toSecretLockInfo).collect(Collectors.toList()));
    }

    public SecretLockRoutesApi getClient() {
        return client;
    }

}
