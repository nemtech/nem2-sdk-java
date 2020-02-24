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

package io.nem.symbol.sdk.model.account;

import java.util.List;

/**
 * Account properties structure describes property information for an account.
 */
public class AccountRestrictions {

    /**
     * The address where the restrictions apply.
     */
    private final Address address;

    /**
     * The restrictions.
     */
    private final List<AccountRestriction> restrictions;

    public AccountRestrictions(Address address, List<AccountRestriction> restrictions) {
        this.address = address;
        this.restrictions = restrictions;
    }

    public Address getAddress() {
        return address;
    }

    public List<AccountRestriction> getRestrictions() {
        return restrictions;
    }
}
