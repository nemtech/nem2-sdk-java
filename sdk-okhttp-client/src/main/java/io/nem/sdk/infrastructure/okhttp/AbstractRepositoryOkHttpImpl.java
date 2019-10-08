/*
 *  Copyright 2019 NEM
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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.Suppliers;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractRepositoryOkHttpImpl {

    private final Supplier<Observable<NetworkType>> networkTypeObservable;

    private final JsonHelper jsonHelper;

    public AbstractRepositoryOkHttpImpl(ApiClient apiClient) {
        networkTypeObservable = Suppliers
            .memoize(() -> new NetworkRepositoryOkHttpImpl(apiClient).getNetworkType().cache());
        jsonHelper = new JsonHelperGson(apiClient.getJSON().getGson());

    }

    public <T> Observable<T> call(Callable<T> callback) {
        Function<? super Throwable, ? extends ObservableSource<? extends T>> resumeFunction = this::onError;
        return Observable.defer(() -> {
            try {
                return Observable.just(callback.call());
            } catch (Exception e) {
                return onError(e);
            }
        }).onErrorResumeNext(resumeFunction);
    }

    public RepositoryCallException exceptionHandling(Throwable e) {
        if (e instanceof RepositoryCallException) {
            return (RepositoryCallException) e;
        }
        return new RepositoryCallException(extractMessageFromException(e), e);
    }

    private String extractMessageFromException(Throwable e) {
        List<String> messages = new ArrayList<>();
        messages.add(ExceptionUtils.getMessage(e));
        if (e instanceof ApiException) {
            messages.add("" + ((ApiException) e).getCode());
            String responseBody = ((ApiException) e).getResponseBody();
            if (responseBody != null) {
                try {
                    // Extracting message from the response body.
                    Object json = jsonHelper.parse(responseBody);
                    messages.add(jsonHelper.getString(json, "code"));
                    messages.add(jsonHelper.getString(json, "message"));
                } catch (IllegalArgumentException ignore) {
                    messages.add(StringUtils.truncate(responseBody, 100));
                }
            }
        }
        return messages.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(" - "));
    }

    public <T> Observable<T> onError(Throwable e) {
        return Observable.error(exceptionHandling(e));
    }


    protected NetworkType getNetworkTypeBlocking() {
        return networkTypeObservable.get().blockingFirst();
    }

    public <T> Observable<T> exceptionHandling(Observable<T> observable) {
        Function<? super Throwable, ? extends ObservableSource<? extends T>> resumeFunction = this::onError;
        return observable.onErrorResumeNext(resumeFunction);
    }


    protected Integer getPageSize(Optional<QueryParams> queryParams) {
        return queryParams.map(QueryParams::getPageSize).orElse(null);
    }

    protected String getId(Optional<QueryParams> queryParams) {
        return queryParams.map(QueryParams::getId).orElse(null);
    }

    protected String getOrder(Optional<QueryParams> queryParams) {
        return queryParams.map(QueryParams::getOrder).orElse(null);
    }


    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }
}
