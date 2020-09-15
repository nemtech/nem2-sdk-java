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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.model.namespace.NamespaceId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Utility main class that uses the nemesis address configured to generate new accounts necessary
 * for the integration tests. Use with caution!!
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AAASetupIntegrationTest extends BaseIntegrationTest {

  private final RepositoryType type = DEFAULT_REPOSITORY_TYPE;

  @Test
  @Order(1)
  void createTestAccount() {
    helper().sendMosaicFromNemesis(type, config().getTestAccount(), false);
    setAddressAlias(type, config().getTestAccount().getAddress(), "testaccount");
    helper().basicSendMosaicFromNemesis(type, NamespaceId.createFromName("testaccount"));
  }

  @Test
  @Order(2)
  void createTestAccount2() {
    helper().sendMosaicFromNemesis(type, config().getTestAccount2(), false);
    setAddressAlias(type, config().getTestAccount2().getAddress(), "testaccount2");
  }

  @Test
  @Order(3)
  void createCosignatoryAccount() {
    helper().sendMosaicFromNemesis(type, config().getCosignatoryAccount(), false);
    setAddressAlias(type, config().getCosignatoryAccount().getAddress(), "cosignatory-account");
  }

  @Test
  @Order(4)
  void createCosignatoryAccount2() {
    helper().sendMosaicFromNemesis(type, config().getCosignatory2Account(), false);
    setAddressAlias(type, config().getCosignatory2Account().getAddress(), "cosignatory-account2");
  }

  @Test
  @Order(5)
  void createMultisigAccountBonded() {
    Assertions.assertNotNull(helper().getMultisigAccount(type));
  }

  @Test
  @Order(6)
  void createMultisigAccountCompleteUsingNemesis() {
    System.out.println(config().getNemesisAccount8().getAddress().encoded());
    helper()
        .createMultisigAccountComplete(
            type,
            config().getNemesisAccount8(),
            config().getNemesisAccount9(),
            config().getNemesisAccount10());
  }

  @Test
  @Order(7)
  void createMultisigAccountBondedUsingNemesis() {
    System.out.println(config().getNemesisAccount7().getAddress().encoded());
    helper()
        .createMultisigAccountBonded(
            type,
            config().getNemesisAccount8(),
            config().getNemesisAccount9(),
            config().getNemesisAccount10());
  }
}
