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
public class SearchCriteria<T extends SearchCriteria<T>> {

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
     * Entry id at which to start pagination. If the ordering parameter is set to DESC, the elements returned precede
     * the identifier. Otherwise, newer elements with respect to the id are returned.  (optional)
     */
    private String offset;

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

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
    /**
     * Sets the order builder style.
     *
     * @param order the order.
     * @return this object.
     */
    public final T order(OrderBy order) {
        this.order = order;
        return getThisBuilder();
    }

    /**
     * Sets the page size builder style.
     *
     * @param pageSize the page size.
     * @return this object.
     */
    public final  T pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return getThisBuilder();
    }

    /**
     * Sets the page number builder style.
     *
     * @param pageNumber the page number.
     * @return this objects.
     */
    public final T pageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return getThisBuilder();
    }

    /**
     * Sets the offset builder style.
     *
     * @param offset the offset.
     * @return this object.
     */
    public final T offset(String offset) {
        this.offset = offset;
        return getThisBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchCriteria<?> that = (SearchCriteria<?>) o;
        return order == that.order && Objects.equals(pageSize, that.pageSize) && Objects
            .equals(pageNumber, that.pageNumber) && Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, pageSize, pageNumber, offset);
    }

    /**
     * @return downcast returning this builder subclass
     */
    private T getThisBuilder() {
        return (T) this;
    }

}
