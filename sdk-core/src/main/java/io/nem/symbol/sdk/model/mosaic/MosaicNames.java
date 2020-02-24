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

package io.nem.symbol.sdk.model.mosaic;

import io.nem.symbol.sdk.model.namespace.NamespaceName;
import java.util.List;

/**
 * The friendly names of one mosaic. The names are namespaces linked using mosaic aliases.
 *
 * @author Fernando Boucquez
 */
public class MosaicNames {

    /**
     * The id of the mosaic.
     */
    private final MosaicId mosaicId;

    /**
     * The names
     */
    private final List<NamespaceName> names;

    /**
     * @param mosaicId the id of the mosaic.
     * @param names the names.
     */
    public MosaicNames(MosaicId mosaicId, List<NamespaceName> names) {
        this.mosaicId = mosaicId;
        this.names = names;
    }

    public MosaicId getMosaicId() {
        return mosaicId;
    }

    public List<NamespaceName> getNames() {
        return names;
    }
}
