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
package io.nem.symbol.sdk.model.transaction;
/** Exception the listener raises after a Transaction Status Error has been sent. */
public class TransactionStatusException extends RuntimeException {

  /** The status error message sent to the listener via web socket. */
  private final TransactionStatusError statusError;

  /**
   * @param caller the exception of the transaction caller.
   * @param statusError the status error message sent to the listener via web socket.
   */
  public TransactionStatusException(Throwable caller, TransactionStatusError statusError) {
    super(statusError.getStatus() + " processing transaction " + statusError.getHash(), caller);
    this.statusError = statusError;
  }

  /** @return The status error message sent to the listener via web socket. */
  public TransactionStatusError getStatusError() {
    return statusError;
  }
}
