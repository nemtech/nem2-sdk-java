package io.nem.sdk.infrastructure;

import com.google.gson.JsonObject;
import io.reactivex.Observable;

public interface HttpClient {
    Observable<HttpResponse> getAbs(String absoluteUrl);
    Observable<HttpResponse> postAbs(String absoluteUrl, JsonObject jsonObject);
    Observable<HttpResponse> putAbs(String absoluteUrl, JsonObject jsonObject);
}
