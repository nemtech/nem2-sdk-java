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

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.util.Objects;

/**
 * Defines the params used to search accounts. With this criteria, you can sort and filter accounts queries using rest.
 */
public class AccountSearchCriteria extends SearchCriteria {

    /**
     * Filer accounts that have balance of this given mosaic id.
     */
    private MosaicId mosaicId;

    /**
     * How accounts are going to be order. If BALANCE is used, mosaic id must be provided.
     */
    private AccountOrderBy orderBy;

    /**
     * Entry id at which to start pagination. If the ordering parameter is set to DESC, the elements returned precede
     * the identifier. Otherwise, newer elements with respect to the id are returned.  (optional)
     */
    private String offset;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    /**
     * Sets the offset builder style.
     *
     * @param offset the new offset
     * @return this criteria.
     */
    public AccountSearchCriteria offset(String offset) {
        this.offset = offset;
        return this;
    }

    public MosaicId getMosaicId() {
        return mosaicId;
    }

    public AccountOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(AccountOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void setMosaicId(MosaicId mosaicId) {
        this.mosaicId = mosaicId;
    }


    public AccountSearchCriteria mosaicId(MosaicId mosaicId) {
        this.mosaicId = mosaicId;
        return this;
    }

    public AccountSearchCriteria orderBy(AccountOrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AccountSearchCriteria that = (AccountSearchCriteria) o;
        return Objects.equals(mosaicId, that.mosaicId) && orderBy == that.orderBy && Objects
            .equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mosaicId, orderBy, offset);
    }
}
