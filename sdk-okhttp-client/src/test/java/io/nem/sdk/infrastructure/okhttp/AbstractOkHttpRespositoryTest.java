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

import com.google.gson.Gson;
import io.nem.core.utils.HttpStatus;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiException;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiResponse;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.model.NetworkTypeDTO;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * Abstract class for all the unit tests related to OkHttp repositories.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractOkHttpRespositoryTest {


    protected ApiClient apiClientMock;

    protected JsonHelper jsonHelper;

    @BeforeEach
    public void setUp() {
        apiClientMock = Mockito.mock(ApiClient.class);
        Mockito.when(apiClientMock.escapeString(Mockito.anyString()))
            .then(a -> a.getArguments()[0]);
        JSON value = new JSON();
        value.setGson(new Gson());
        Mockito.when(apiClientMock.getJSON()).thenReturn(value);

        jsonHelper = new JsonHelperGson(value.getGson());
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

    protected <T> void mockRemoteCall(T value) throws ApiException {
        Map<String, List<String>> headers = Collections.emptyMap();
        ApiResponse<T> apiResponse = new ApiResponse<>(200, headers, value);
        Mockito.when(apiClientMock.<T>execute(Mockito.any(Call.class),
            Mockito.any(Type.class))).thenReturn(apiResponse);
    }

    /**
     * Mocks the api client telling that the next time there is remote call, an error should be
     * returned. The mocked response body is the expected json from the catapult rest error
     * handler.
     *
     * @param statusCode the status code of the response (404 for example)
     * @param message the error message that will be returned in the body.
     */
    protected void mockErrorCode(final int statusCode, final String message) throws ApiException {

        String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
        Map<String, String> errorBody = new HashMap<>();
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
    protected void mockErrorCodeRawResponse(int statusCode, String errorResponse)
        throws ApiException {
        String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
        Map<String, List<String>> headers = Collections.emptyMap();
        ApiException exception = new ApiException(reasonPhrase,
            statusCode, headers, errorResponse);

        Mockito.when(apiClientMock.execute(Mockito.any(Call.class),
            Mockito.any(Type.class))).thenThrow(exception);
    }


    protected final NetworkType resolveNetworkType() throws ApiException {
        NetworkTypeDTO networkTypeDTO = new NetworkTypeDTO();
        networkTypeDTO.setName("mijinTest");
        mockRemoteCall(networkTypeDTO);
        NetworkType networkType = getRepository().getNetworkTypeBlocking();
        Assertions.assertEquals(NetworkType.MIJIN_TEST, networkType);
        return networkType;
    }

    protected abstract AbstractRepositoryOkHttpImpl getRepository();


}
