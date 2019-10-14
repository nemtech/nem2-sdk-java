/*
 * Copyright 2018 NEM
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

package io.nem.sdk.api;

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.reactivex.Observable;
import java.util.List;

/**
 * Mosaic interface repository.
 *
 * @since 1.0
 */
public interface MosaicRepository {

    /**
     * Gets a MosaicInfo for a given mosaicId
     *
     * @param mosaicId {@link MosaicId}
     * @return Observable of {@link MosaicInfo}
     */
    Observable<MosaicInfo> getMosaic(MosaicId mosaicId);

    /**
     * Gets MosaicInfo for different mosaicIds.
     *
     * @param mosaicIds {@link List} of {@link MosaicId}
     * @return {@link Observable} of {@link MosaicInfo} List
     */
    Observable<List<MosaicInfo>> getMosaics(List<MosaicId> mosaicIds);

    /**
     * Gets MosaicNames for different accounts. The names are namespaces linked using mosaic
     * aliases.
     *
     * @param mosaicIds {@link List} of {@link MosaicId}
     * @return {@link Observable} of {@link MosaicNames} List
     */

    Observable<List<MosaicNames>> getMosaicsNames(List<MosaicId> mosaicIds);

}
