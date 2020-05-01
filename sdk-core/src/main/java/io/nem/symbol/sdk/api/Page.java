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

import java.util.List;

/**
 * It represents a page of results after a repository search call.
 *
 * @param <E> then model type.
 */
public class Page<E> {

    /**
     * The page's entities.
     */
    private final List<E> data;

    /**
     * The current page number. 1 means first page
     */
    private final Integer pageNumber;

    /**
     * The page size.
     */
    private final Integer pageSize;

    /**
     * The total entries.
     */
    private final Integer totalEntries;

    /**
     * The total pages.
     */
    private final Integer totalPages;

    /**
     * Constructor.
     *
     * @param data the page data
     * @param pageNumber the current page number starting from 1.
     * @param pageSize the page size.
     * @param totalEntries the total entries.
     * @param totalPages the total pages for the given criteria.
     */
    public Page(List<E> data, Integer pageNumber, Integer pageSize, Integer totalEntries,
        Integer totalPages) {
        this.data = data;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalEntries = totalEntries;
        this.totalPages = totalPages;
    }

    /**
     * @return The page data.
     */
    public List<E> getData() {
        return data;
    }

    /**
     * @return the current page number starting from 1.
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * @return the page size.
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @return the total entries.
     */
    public Integer getTotalEntries() {
        return totalEntries;
    }

    /**
     * @return the total pages.
     */
    public Integer getTotalPages() {
        return totalPages;
    }
}
