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
package io.nem.symbol.sdk.model.finalization;

/** Parent publickey signature pair */
public class ParentPublicKeySignaturePair {

  /** Parent public key. */
  private final String parentPublicKey;
  /** Signature. */
  private final String signature;

  public ParentPublicKeySignaturePair(String parentPublicKey, String signature) {
    this.parentPublicKey = parentPublicKey;
    this.signature = signature;
  }

  public String getParentPublicKey() {
    return parentPublicKey;
  }

  public String getSignature() {
    return signature;
  }
}
