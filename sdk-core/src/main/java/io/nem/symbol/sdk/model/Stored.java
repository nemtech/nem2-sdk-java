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
package io.nem.symbol.sdk.model;

import java.util.Optional;

/**
 * An entity that's stored in the server database.
 *
 * <p>Ideally, clients shouldn't care or use the entities' database Ids. Database Ids can be
 * different between different nodes.
 *
 * <p>Clients should use the entities natural id like account address, mosaic id, namespace id,
 * transaction hash, etc.
 *
 * <p>The database id is currently being used as the default sort by value and it may be used when
 * setting offset values in searches.
 */
public interface Stored {

  /**
   * Returns database record id of the entity.
   *
   * @return The record id of the entity if it's known.
   */
  Optional<String> getRecordId();
}
