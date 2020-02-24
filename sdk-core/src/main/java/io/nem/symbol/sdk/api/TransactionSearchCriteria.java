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

import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.Collections;
import java.util.List;

/**
 * Defines the params used to search transactions. With is criteria, you can sort and filter
 * transactions queries using rest.
 */
public class TransactionSearchCriteria {

    /**
     * Number of transactions to return for each request. (optional, default to 10)
     */
    private Integer pageSize;

    /**
     * Transaction identifier up to which transactions are returned. (optional)
     */
    private String id;

    /**
     * Ordering criteria: * -id - Descending order by id. * id - Ascending order by id.  (optional,
     * default to &quot;-id&quot;)
     */
    private String order;

    /**
     * Transaction types to filter by. (optional)
     */
    private List<TransactionType> transactionTypes = Collections.emptyList();

    public Integer getPageSize() {
        return pageSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }

    public void setTransactionTypes(
        List<TransactionType> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public List<TransactionType> getTransactionTypes() {
        return transactionTypes;
    }


    /**
     * Sets the id filter returning this criteria to nest criteria configuration.
     *
     * @param id Number of transactions to return for each request. (optional, default to 10)
     * @return this criteria
     */
    public TransactionSearchCriteria id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the pageSize returning this criteria to nest criteria configuration.
     *
     * @param pageSize Number of transactions to return for each request. (optional, default to 10)
     * @return this criteria
     */
    public TransactionSearchCriteria pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Sets the order returning this criteria to nest criteria configuration.
     *
     * @param order Ordering criteria: * -id - Descending order by id. * id - Ascending order by id.
     * (optional, default to &quot;-id&quot;)
     * @return this criteria
     */
    public TransactionSearchCriteria order(String order) {
        this.order = order;
        return this;
    }

    /**
     * Sets the transactionTypes filter returning this criteria to nest criteria configuration.
     *
     * @param transactionTypes Transaction type to filter by. (optional)
     * @return this criteria
     */
    public TransactionSearchCriteria transactionTypes(List<TransactionType> transactionTypes) {
        this.transactionTypes = transactionTypes;
        return this;
    }


}
