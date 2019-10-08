/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.metadata;

/**
 * A mosaic describes an instance of a mosaic definition. Mosaics can be transferred by means of a
 * transfer transaction.
 */
public class Metadata {

    /**
     * The metadata id
     */
    private final String id;

    /**
     * The metadata entry.
     */
    private final MetadataEntry metadataEntry;


    /**
     * The constructor.
     *
     * @param id the metadata id.
     * @param metadataEntry the metadata entry with the information.
     */
    public Metadata(String id, MetadataEntry metadataEntry) {
        this.id = id;
        this.metadataEntry = metadataEntry;
    }

    public String getId() {
        return id;
    }

    public MetadataEntry getMetadataEntry() {
        return metadataEntry;
    }
}
