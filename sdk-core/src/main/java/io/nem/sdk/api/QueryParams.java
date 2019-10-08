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

/**
 * The query params structure describes pagination params for requests.
 *
 * @since 1.0
 */
public class QueryParams {

    private final Integer pageSize;

    private final String id;

    private final String order;

    public QueryParams(Integer pageSize, String id, String order) {
        this.pageSize = pageSize != null && (pageSize >= 10 && pageSize <= 100) ? pageSize : 10;
        this.id = id;
        this.order = order;
    }

    public QueryParams(Integer pageSize, String id) {
        this(pageSize, id, null);
    }

    /**
     * Returns id after which we want objects to be returned.
     *
     * @return object id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns page size between 10 and 100, otherwise 10.
     *
     * @return page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Return the field used to sort the results. Example 'id' . If prefixed with '-', the order
     * will be reversed. Example '-id'.
     *
     * @return the field used to sort the results
     */
    public String getOrder() {
        return order;
    }

}
