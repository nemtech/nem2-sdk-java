/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.transaction.*;
import io.reactivex.Observable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Transaction http repository.
 *
 * @since 1.0
 */
public class TransactionHttp extends Http implements TransactionRepository {
    public TransactionHttp(String host) throws MalformedURLException {
        this(host + "/transaction/", new NetworkHttp(host));
    }

    public TransactionHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host, networkHttp);
    }

    @Override
    public Observable<Transaction> getTransaction(String transactionHash) {
        return this.client
                .getAbs(this.url + transactionHash)
                .map(Http::mapStringOrError)
                .map(str -> new Gson().fromJson(str, JsonObject.class))
                .map(new TransactionMapping());
    }

    @Override
    public Observable<List<Transaction>> getTransactions(List<String> transactionHashes) {
        JsonObject requestBody = new JsonObject();
        JsonArray transactionHashJsonArray = new JsonArray();
        for(String transactionHash: transactionHashes) {
            transactionHashJsonArray.add(transactionHash);
        }

        requestBody.add("transactionIds", transactionHashJsonArray);
        return this.client
                .postAbs(this.url.toString(), requestBody)
                .map(Http::mapStringOrError)
                .map(str -> StreamSupport.stream(new Gson().fromJson(str, JsonArray.class).spliterator(), false).map(s -> (JsonObject) s).collect(Collectors.toList()))
                .flatMapIterable(item -> item)
                .map(new TransactionMapping())
                .toList()
                .toObservable();
    }


    @Override
    public Observable<TransactionStatus> getTransactionStatus(String transactionHash) {
        return this.client
                .getAbs(this.url + transactionHash + "/status")
                .map(Http::mapStringOrError)
                .map(str -> objectMapper.readValue(str, TransactionStatusDTO.class))
                .map(transactionStatusDTO -> new TransactionStatus(transactionStatusDTO.getGroup(),
                        transactionStatusDTO.getStatus(),
                        transactionStatusDTO.getHash(),
                        new Deadline(transactionStatusDTO.getDeadline().extractIntArray()),
                        transactionStatusDTO.getHeight().extractIntArray()));
    }

    @Override
    public Observable<List<TransactionStatus>> getTransactionStatuses(List<String> transactionHashes) {
        JsonObject requestBody = new JsonObject();

        JsonArray transactionHashJsonArray = new JsonArray();
        for(String transactionHash: transactionHashes) {
            transactionHashJsonArray.add(transactionHash);
        }

        requestBody.add("hashes", transactionHashJsonArray);
        return this.client
                .postAbs(this.url + "/statuses", requestBody)
                .map(Http::mapStringOrError)
                .map(str -> objectMapper.<List<TransactionStatusDTO>>readValue(str, new TypeReference<List<TransactionStatusDTO>>() {
                }))
                .flatMapIterable(item -> item)
                .map(transactionStatusDTO -> new TransactionStatus(transactionStatusDTO.getGroup(),
                        transactionStatusDTO.getStatus(),
                        transactionStatusDTO.getHash(),
                        new Deadline(transactionStatusDTO.getDeadline().extractIntArray()),
                        transactionStatusDTO.getHeight().extractIntArray()))
                .toList()
                .toObservable();
    }

    @Override
    public Observable<TransactionAnnounceResponse> announce(SignedTransaction signedTransaction) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("payload", signedTransaction.getPayload());
        return this.client
                .putAbs(this.url.toString(), requestBody)
                .map(Http::mapStringOrError)
                .map(str -> new TransactionAnnounceResponse(new Gson().fromJson(str, JsonObject.class).get("message").getAsString()));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBonded(SignedTransaction signedTransaction) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("payload", signedTransaction.getPayload());
        return this.client
                .putAbs(this.url + "/partial", requestBody)
                .map(Http::mapStringOrError)
                .map(str -> new TransactionAnnounceResponse(new Gson().fromJson(str, JsonObject.class).get("message").getAsString()));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(CosignatureSignedTransaction cosignatureSignedTransaction) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("parentHash", cosignatureSignedTransaction.getParentHash());
        requestBody.addProperty("signature", cosignatureSignedTransaction.getSignature());
        requestBody.addProperty("signer", cosignatureSignedTransaction.getSigner());
        return this.client
                .putAbs(this.url + "/cosignature", requestBody)
                .map(Http::mapStringOrError)
                .map(str -> new TransactionAnnounceResponse(new Gson().fromJson(str, JsonObject.class).get("message").getAsString()));
    }
}
