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

package io.nem.sdk.api;

/**
 * General exception raised when there is an error during a repository call (like a rest call
 * exception)
 *
 * This exception hides the implementation specific exception (like jersey2, vertx or okhttp
 * exceptions). The original exception will be part of the cause.
 *
 * @author Fernando Boucquez
 */
public class RepositoryCallException extends RuntimeException {

    /**
     * The http status error code.
     */
    private final int statusCode;

    /**
     * @param message the message. It may be resolved from the response body an status of a failed
     * request.
     * @param statusCode the http status code (like 404 or 500).
     * @param cause the original exception, probably linked to the specific implementation.
     */
    public RepositoryCallException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * @return the http status code error.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
