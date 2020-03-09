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

import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.namespace.NamespaceId;

/**
 * Unresolved address is used when the referenced account can be accessed via an {@link Address} or
 * a {@link NamespaceId}
 */
public interface UnresolvedAddress {

    /**
     * @param networkType the network type.
     * @return the encoded address or namespace id. Note that namespace id get the hex reversed and
     * zero padded. See {@link SerializationUtils}
     */
    String encoded(NetworkType networkType);

}
