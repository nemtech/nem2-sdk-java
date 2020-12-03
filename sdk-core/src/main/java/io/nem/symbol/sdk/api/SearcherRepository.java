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
package io.nem.symbol.sdk.api;

/**
 * Utility helper that stream pages of searches into an Observable.
 *
 * <p>A streamer will help users to walk through searches without knowing the underlying pagination
 * implementation.
 */
public interface SearcherRepository<E, C extends SearchCriteria<C>> extends Searcher<E, C> {

  /** @return a new pagination streamer for these objects. */
  default PaginationStreamer<E, C> streamer() {
    return new PaginationStreamer<E, C>(this);
  }
}
