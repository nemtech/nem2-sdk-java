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

import com.google.gson.Gson;
import io.nem.symbol.core.utils.HttpStatus;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiException;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiResponse;
import io.reactivex.Observable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Abstract class for all the unit tests related to OkHttp repositories.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractOkHttpRespositoryTest {

  protected ApiClient apiClientMock;

  protected JsonHelper jsonHelper;

  protected final NetworkType networkType = NetworkType.MIJIN_TEST;

  protected final Observable<NetworkType> networkTypeObservable = Observable.just(networkType);

  @BeforeEach
  public void setUp() {
    Gson gson = JsonHelperGson.creatGson(false);
    ApiClient client = new ApiClient();
    client.getJSON().setGson(gson);
    apiClientMock = Mockito.spy(client);
    jsonHelper = new JsonHelperGson(gson);
  }

  protected String encodeAddress(Address address) {
    return address.encoded();
  }

  /**
   * Mocks the api client telling what would it be the next response when any remote call is
   * executed.
   *
   * @param <T> tye type of the remote response.
   * @param value the next mocked remote call response
   * @return a {@link ArgumentCaptor} of the call.
   */
  protected <T> ArgumentCaptor<Object> mockRemoteCall(T value) throws ApiException {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    Map<String, List<String>> headers = Collections.emptyMap();
    ApiResponse<T> apiResponse = new ApiResponse<>(200, headers, value);

    Mockito.doCallRealMethod()
        .when(apiClientMock)
        .buildCall(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyList(),
            Mockito.anyList(),
            captor.capture(),
            Mockito.anyMap(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any());

    Mockito.doReturn(apiResponse)
        .when(apiClientMock)
        .execute(Mockito.any(), Mockito.any(Type.class));
    return captor;
  }

  /**
   * Mocks the api client telling that the next time there is remote call, an error should be
   * returned. The mocked response body is the expected json from the catapult rest error handler.
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
    ApiException exception = new ApiException(reasonPhrase, statusCode, headers, errorResponse);

    Mockito.doThrow(exception).when(apiClientMock).execute(Mockito.any(), Mockito.any(Type.class));
  }

  protected abstract AbstractRepositoryOkHttpImpl getRepository();
}
