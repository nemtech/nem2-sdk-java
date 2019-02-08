package io.nem.sdk.infrastructure;

import java.io.IOException;

public interface HttpResponse {
    int getCode();
    String getStatusMessage();
    String getBodyString() throws IOException;
}
