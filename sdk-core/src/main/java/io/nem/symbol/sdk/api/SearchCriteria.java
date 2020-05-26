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

import java.util.Objects;

/**
 * Basic option used to search pages of entities.
 */
public class SearchCriteria {

    /**
     * Sort responses in ascending or descending order based on the collection property set on the
     * param ''orderBy''. If the request does not specify ''orderBy'', REST returns the collection
     * ordered by id.  (optional, default to desc)
     */
    private OrderBy order;

    /**
     * Number of entities to return for each request. (optional, default to 10)
     */
    private Integer pageSize;

    /**
     * Filter by page number. (optional, default to 1)
     */
    private Integer pageNumber;

    /**
     * @return the order.
     */
    public OrderBy getOrder() {
        return order;
    }

    /**
     * @return page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @return page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setOrder(OrderBy order) {
        this.order = order;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Sets the order builder style.
     *
     * @param order the order.
     * @return this object.
     */
    public SearchCriteria order(OrderBy order) {
        this.order = order;
        return this;
    }

    /**
     * Sets the page size builder style.
     *
     * @param pageSize the page size.
     * @return this object.
     */
    public SearchCriteria pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Sets the page number builder style.
     *
     * @param pageNumber the page number.
     * @return this objects.
     */
    public SearchCriteria pageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
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
        SearchCriteria that = (SearchCriteria) o;
        return order == that.order &&
            Objects.equals(pageSize, that.pageSize) &&
            Objects.equals(pageNumber, that.pageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, pageSize, pageNumber);
    }
}
