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

import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.reactivex.Observable;

/** A helper object that streams {@link MosaicRestriction} using the search. */
public class MosaicRestrictionPaginationStreamer
    extends PaginationStreamer<MosaicRestriction<?>, MosaicRestrictionSearchCriteria> {

  /**
   * Constructor
   *
   * @param searcher the MosaicRestriction repository that will perform the searches
   */
  public MosaicRestrictionPaginationStreamer(
      Searcher<MosaicRestriction<?>, MosaicRestrictionSearchCriteria> searcher) {
    super(searcher);
  }

  /**
   * Searches address restrictions
   *
   * @param searcher the searcher
   * @param criteria the criteria
   * @return an observable of MosaicAddressRestriction
   */
  public static Observable<MosaicAddressRestriction> address(
      Searcher<MosaicRestriction<?>, MosaicRestrictionSearchCriteria> searcher,
      MosaicRestrictionSearchCriteria criteria) {
    return new MosaicRestrictionPaginationStreamer(searcher)
        .search(criteria.entryType(MosaicRestrictionEntryType.ADDRESS))
        .map(a -> (MosaicAddressRestriction) a);
  }

  /**
   * Searches global restrictions
   *
   * @param searcher the searcher
   * @param criteria the criteria
   * @return an observable of MosaicAddressRestriction
   */
  public static Observable<MosaicGlobalRestriction> global(
      Searcher<MosaicRestriction<?>, MosaicRestrictionSearchCriteria> searcher,
      MosaicRestrictionSearchCriteria criteria) {
    return new MosaicRestrictionPaginationStreamer(searcher)
        .search(criteria.entryType(MosaicRestrictionEntryType.GLOBAL))
        .map(a -> (MosaicGlobalRestriction) a);
  }
}
