package io.nem.sdk.infrastructure;

import com.google.gson.JsonObject;
import io.reactivex.Observable;
import okhttp3.*;

import java.io.IOException;

public class OkHttpHttpClient implements HttpClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private OkHttpClient client;
    OkHttpHttpClient() {
        client = new OkHttpClient.Builder().build();
    }

    @Override
    public Observable<HttpResponse> getAbs(String absoluteUrl) {
        final Request request = new Request.Builder()
                .get()
                .url(absoluteUrl)
                .build();

        return createHttpResponseObservable(request);
    }

    @Override
    public Observable<HttpResponse> postAbs(String absoluteUrl, JsonObject jsonObject) {
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        final Request request = new Request.Builder()
                .post(body)
                .url(absoluteUrl)
                .build();

        return createHttpResponseObservable(request);

    }

    @Override
    public Observable<HttpResponse> putAbs(String absoluteUrl, JsonObject jsonObject) {
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        final Request request = new Request.Builder()
                .put(body)
                .url(absoluteUrl)
                .build();

        return createHttpResponseObservable(request);
    }

    private Observable<HttpResponse> createHttpResponseObservable(Request request) {
        return Observable.create(emitter -> {
            try {
                Response response = client.newCall(request).execute();
                emitter.onNext(new OkHttpResponse(response));
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    class OkHttpResponse implements HttpResponse {
        final Response response;

        OkHttpResponse(Response response) {
            this.response = response;
        }

        @Override
        public int getCode() {
            return response.code();
        }

        @Override
        public String getStatusMessage() {
            return response.message();
        }

        @Override
        public String getBodyString() throws IOException {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return body.string();
        }
    }
}
