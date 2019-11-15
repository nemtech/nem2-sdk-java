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

package io.nem.sdk.infrastructure.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.core.utils.HttpStatus;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.invoker.ApiException;
import io.nem.sdk.openapi.vertx.invoker.Pair;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Abstract class for all the unit tests related to OkHttp repositories.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractVertxRespositoryTest {


    protected ApiClient apiClientMock;

    protected JsonHelper jsonHelper;

    protected final NetworkType networkType = NetworkType.MIJIN_TEST;

    protected final Observable<NetworkType> networkTypeObservable = Observable.just(networkType);

    @BeforeEach
    public void setUp() {
        apiClientMock = Mockito.mock(ApiClient.class);
        ObjectMapper objectMapper = JsonHelperJackson2.configureMapper(new ObjectMapper());
        jsonHelper = new JsonHelperJackson2(objectMapper);
        Mockito.when(apiClientMock.getObjectMapper()).thenReturn(objectMapper);
    }

    protected String encodeAddress(Address address) {
        return address.encoded();
    }

    /**
     * Mocks the api client telling what would it be the next response when any remote call is
     * executed.
     *
     * @param value the next mocked remote call response
     * @param <T> tye type of the remote response.
     */
    protected <T> void mockRemoteCall(T value) {
        Mockito.doAnswer((Answer<Void>) invocationOnMock -> {

            Handler<AsyncResult<T>> resultHandler = (Handler<AsyncResult<T>>) invocationOnMock
                .getArguments()[invocationOnMock.getArguments().length - 1];
            resultHandler.handle(Future.succeededFuture(value));

            return null;
        }).when(apiClientMock)
            .invokeAPI(Mockito.anyString(), Mockito.anyString(), Mockito.anyListOf(Pair.class),
                Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());


    }


    /**
     * Mocks the api client telling that the next time there is remote call, an error should be
     * returned. The mocked response body is the expected json from the catapult rest error
     * handler.
     *
     * @param statusCode the status code of the response (404 for example)
     * @param message the error message that will be returned in the body.
     */
    protected void mockErrorCode(final int statusCode, final String message) {
        Map<String, String> errorBody = new HashMap<>();

        String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
        errorBody.put("code", "Code " + reasonPhrase);
        errorBody.put("message", message);
        String errorResponse = jsonHelper.print(errorBody);
        mockErrorCodeRawResponse(statusCode, errorResponse);
    }

    /**
     * Mocks the api client telling that the next time there is remote call, an error should be
     * returned.
     *
     * @param statusCode the status code of the response (404 for example)
     * @param errorResponse the raw response, it may or may not be a json string.
     */
    protected void mockErrorCodeRawResponse(int statusCode, String errorResponse) {
        String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
        VertxHttpHeaders headers = new VertxHttpHeaders();
        ApiException exception = new ApiException(reasonPhrase, statusCode, headers,
            errorResponse);

        Mockito.doAnswer((Answer<Void>) invocationOnMock -> {

            Handler<AsyncResult<Object>> resultHandler = (Handler<AsyncResult<Object>>) invocationOnMock
                .getArguments()[invocationOnMock.getArguments().length - 1];
            resultHandler.handle(Future.failedFuture(exception));

            return null;
        }).when(apiClientMock)
            .invokeAPI(Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
    }


}
