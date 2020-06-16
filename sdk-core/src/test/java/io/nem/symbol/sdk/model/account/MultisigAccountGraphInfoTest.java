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

import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class MultisigAccountGraphInfoTest {

    @Test
    void returnTheLevels() {
        Map<Integer, List<MultisigAccountInfo>> info = new HashMap<>();
        MultisigAccountInfo multisigAccountInfo =
            new MultisigAccountInfo(
                new PublicAccount(
                    "5D58EC16F07BF00BDE9B040E7451A37F9908C59E143A01438C04345D8E9DDF39",
                    NetworkType.MIJIN_TEST).getAddress(),
                1,
                1,
                Collections.singletonList(
                    new PublicAccount(
                        "1674016C27FE2C2EB5DFA73996FA54A183B38AED0AA64F756A3918BAF08E061B",
                        NetworkType.MIJIN_TEST).getAddress()),
                Collections.EMPTY_LIST);
        info.put(-3, Collections.singletonList(multisigAccountInfo));
        MultisigAccountGraphInfo multisigAccountGraphInfo = new MultisigAccountGraphInfo(info);
        assertEquals(
            Stream.of(-3).collect(Collectors.toSet()), multisigAccountGraphInfo.getLevelsNumber());
        assertEquals(
            multisigAccountInfo, multisigAccountGraphInfo.getMultisigEntries().get(-3).get(0));
    }
}
