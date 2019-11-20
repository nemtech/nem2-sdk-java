/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceName;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountNamesTest {

    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    void createAccountNames() {
        Address address = Address.createFromRawAddress("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26");

        List<NamespaceName> namespaceNames = Arrays.asList(
            new NamespaceName("accountalias", networkType),
            new NamespaceName("anotheralias", networkType));

        AccountNames names = new AccountNames(address, namespaceNames);
        assertEquals(address, names.getAddress());
        assertEquals(namespaceNames, names.getNames());
    }
}
