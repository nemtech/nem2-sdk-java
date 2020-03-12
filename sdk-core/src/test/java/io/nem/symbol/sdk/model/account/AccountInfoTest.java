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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountInfoTest {

    @Test
    void shouldCreateAccountInfoViaConstructor() {
        List<Mosaic> mosaics =
            Collections.singletonList(NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)));
        AccountInfo accountInfo =
            new AccountInfo(
                Address.createFromRawAddress("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26"),
                new BigInteger("964"),
                "cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb",
                new BigInteger("966"),
                new BigInteger("777"),
                new BigInteger("0"),
                mosaics, AccountType.REMOTE_UNLINKED);

        assertEquals(
            Address.createFromRawAddress("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26"),
            accountInfo.getAddress());
        assertEquals(new BigInteger("964"), accountInfo.getAddressHeight());
        assertEquals(
            "cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb",
            accountInfo.getPublicKey());
        assertEquals(new BigInteger("966"), accountInfo.getPublicKeyHeight());
        assertEquals(new BigInteger("777"), accountInfo.getImportances().get(0).getValue());
        assertEquals(new BigInteger("0"), accountInfo.getImportances().get(0).getHeight());
        assertEquals(mosaics, accountInfo.getMosaics());
        assertEquals(
            PublicAccount.createFromPublicKey(
                "cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb",
                NetworkType.MIJIN_TEST),
            accountInfo.getPublicAccount());

        assertEquals(AccountType.REMOTE_UNLINKED,
            accountInfo.getAccountType());
    }
}
