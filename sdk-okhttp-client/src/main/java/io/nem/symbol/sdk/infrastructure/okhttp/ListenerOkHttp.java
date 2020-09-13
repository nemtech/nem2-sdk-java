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
import com.google.gson.JsonObject;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.infrastructure.ListenerBase;
import io.nem.symbol.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.FinalizedBlockDTO;
import io.reactivex.Observable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * OkHttp implementations of the {@link Listener}.
 *
 * @since 1.0
 */
public class ListenerOkHttp extends ListenerBase implements Listener {

  private final URL url;

  private final OkHttpClient httpClient;

  private final TransactionMapper transactionMapper;

  private WebSocket webSocket;

  /**
   * @param httpClient the ok http client
   * @param url nis host
   * @param gson gson's gson.
   * @param namespaceRepository the namespace repository used to resolve alias.
   * @param networkTypeObservable the network type;
   */
  public ListenerOkHttp(
      OkHttpClient httpClient,
      String url,
      Gson gson,
      NamespaceRepository namespaceRepository,
      Observable<NetworkType> networkTypeObservable) {
    super(new JsonHelperGson(gson), namespaceRepository, networkTypeObservable);
    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "Parameter '" + url + "' is not a valid URL. " + ExceptionUtils.getMessage(e));
    }
    this.httpClient = httpClient;
    this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
  }

  /** @return a {@link CompletableFuture} that resolves when the websocket connection is opened */
  @Override
  public CompletableFuture<Void> open() {

    CompletableFuture<Void> future = new CompletableFuture<>();
    if (this.webSocket != null) {
      return CompletableFuture.completedFuture(null);
    }
    Request webSocketRequest =
        new Request.Builder().url(checkTrailingSlash(url.toString()) + "ws").build();
    WebSocketListener webSocketListener =
        new WebSocketListener() {
          @Override
          public void onMessage(WebSocket webSocket, String text) {
            handle(getJsonHelper().parse(text, JsonObject.class), future);
          }
        };
    this.webSocket = httpClient.newWebSocket(webSocketRequest, webSocketListener);
    return future;
  }

  private String checkTrailingSlash(String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  @Override
  protected FinalizedBlock toFinalizedBlock(Object blockInfoDTO) {
    return ChainRepositoryOkHttpImpl.toFinalizedBlock(
        getJsonHelper().convert(blockInfoDTO, FinalizedBlockDTO.class));
  }

  @Override
  protected BlockInfo toBlockInfo(Object blockInfoDTO) {
    return BlockRepositoryOkHttpImpl.toBlockInfo(
        getJsonHelper().convert(blockInfoDTO, BlockInfoDTO.class));
  }

  @Override
  protected Transaction toTransaction(TransactionGroup group, Object transactionInfo) {
    return transactionMapper.mapToFactoryFromDto(transactionInfo).group(group).build();
  }

  @Override
  protected CosignatureSignedTransaction toCosignatureSignedTransaction(
      Object cosignatureJson, NetworkType networkType) {
    Cosignature cosignature = getJsonHelper().convert(cosignatureJson, Cosignature.class);
    return new CosignatureSignedTransaction(
        cosignature.getVersion(),
        cosignature.getParentHash(),
        cosignature.getSignature(),
        PublicAccount.createFromPublicKey(cosignature.getSignerPublicKey(), networkType));
  }

  /** Close webSocket connection */
  @Override
  public void close() {
    if (this.webSocket != null) {
      setUid(null);
      this.webSocket.close(1000, null);
      this.webSocket = null;
    }
  }

  protected void subscribeTo(String channel) {
    final ListenerSubscribeMessage subscribeMessage =
        new ListenerSubscribeMessage(this.getUid(), channel);
    this.webSocket.send(getJsonHelper().print(subscribeMessage));
  }
}
