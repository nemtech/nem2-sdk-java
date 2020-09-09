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
package io.nem.symbol.sdk.model.account;

import java.util.List;
import java.util.Optional;

/** Supplemental Public Keys */
public class SupplementalAccountKeys {

  /** Linked public key if any */
  private final Optional<String> linked;

  /** Node public key if any */
  private final Optional<String> node;

  /** VRF public key if any */
  private final Optional<String> vrf;

  /** Veys public keys if any */
  private final List<AccountLinkVotingKey> voting;

  public SupplementalAccountKeys(
      Optional<String> linked,
      Optional<String> node,
      Optional<String> vrf,
      List<AccountLinkVotingKey> voting) {
    this.linked = linked;
    this.node = node;
    this.vrf = vrf;
    this.voting = voting;
  }

  public Optional<String> getLinked() {
    return linked;
  }

  public Optional<String> getNode() {
    return node;
  }

  public Optional<String> getVrf() {
    return vrf;
  }

  public List<AccountLinkVotingKey> getVoting() {
    return voting;
  }
}
