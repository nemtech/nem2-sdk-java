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
package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.namespace.NamespaceName;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class MosaicNamesTest {

  @Test
  void createMosaicNames() {
    MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));
    List<NamespaceName> namespaceNames =
        Arrays.asList(new NamespaceName("xem"), new NamespaceName("anotheralias"));
    MosaicNames names = new MosaicNames(mosaicId, namespaceNames);
    assertEquals(mosaicId, names.getMosaicId());

    assertEquals(namespaceNames, names.getNames());
  }
}
