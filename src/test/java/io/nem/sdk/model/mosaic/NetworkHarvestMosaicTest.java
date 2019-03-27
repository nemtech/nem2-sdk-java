/*
 * Copyright 2019 NEM
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
package io.nem.sdk.model.mosaic;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkHarvestMosaicTest {
    @Test
    void shouldCreateNetworkHarvestMosaicViaConstructor() {
        NetworkHarvestMosaic networkHarvestMosaic = new NetworkHarvestMosaic(BigInteger.valueOf(0));
        assertEquals(BigInteger.valueOf(0), networkHarvestMosaic.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, networkHarvestMosaic.getId());
    }

    @Test
    void shouldCreateRelativeNetworkHarvestMosaic() {
        NetworkHarvestMosaic networkHarvestMosaic = NetworkHarvestMosaic.createRelative(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1000000), networkHarvestMosaic.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, networkHarvestMosaic.getId());
    }

    @Test
    void shouldCreateAbsoluteNetworkHarvestMosaic() {
        NetworkHarvestMosaic networkHarvestMosaic = NetworkHarvestMosaic.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), networkHarvestMosaic.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, networkHarvestMosaic.getId());
    }
}
