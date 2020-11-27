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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.catapult.builders.MultisigEntryBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MultisigAccountInfoTest {

  private final Address address1 =
      new PublicAccount(
              "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
              NetworkType.MIJIN_TEST)
          .getAddress();
  private final Address address2 =
      new PublicAccount(
              "846b4439154579a5903b1459c9cf69cb8153f6d0110a7a0ed61de29ae4810bf2",
              NetworkType.MIJIN_TEST)
          .getAddress();
  private final Address address3 =
      new PublicAccount(
              "cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb",
              NetworkType.MIJIN_TEST)
          .getAddress();
  private final Address address4 =
      new PublicAccount(
              "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
              NetworkType.MIJIN_TEST)
          .getAddress();

  @Test
  void shouldBeCreated() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Collections.singletonList(address2), Collections.emptyList());
    assertEquals(1, multisigAccountInfo.getMinRemoval());
    assertEquals(2, multisigAccountInfo.getMinApproval());
    assertEquals(1, multisigAccountInfo.getCosignatoryAddresses().size());
    assertEquals(address1, multisigAccountInfo.getAccountAddress());
  }

  @Test
  void isCosignerShouldReturnTrueWhenTheAccountIsInTheCosignatoriesList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Arrays.asList(address2, address3), Collections.emptyList());

    assertTrue(multisigAccountInfo.hasCosigner(address2));
  }

  @Test
  void isCosignatoryShouldReturnFalseWhenTheAccountIsNotInTHeCosignatoriesList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Arrays.asList(address2, address3), Collections.emptyList());

    assertFalse(multisigAccountInfo.hasCosigner(address4));
  }

  @Test
  void isCosignerOfMultisigAccountShouldReturnTrueWhenItContainsThatAccountInMultisigList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Collections.emptyList(), Arrays.asList(address2, address3));

    assertTrue(multisigAccountInfo.isCosignerOfMultisigAccount(address2));
  }

  @Test
  void
      isCosignerOfMultisigAccountShouldReturnFalseWhenItDoesNotContainsThatAccountInMultisigList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Collections.emptyList(), Arrays.asList(address2, address3));

    assertFalse(multisigAccountInfo.isCosignerOfMultisigAccount(address4));
  }

  @Test
  void returnCosignersList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Arrays.asList(address2, address3), Collections.emptyList());

    assertEquals(Arrays.asList(address2, address3), multisigAccountInfo.getCosignatoryAddresses());
  }

  @Test
  void returnMultisigList() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 2, 1, Collections.emptyList(), Arrays.asList(address2, address3));

    assertEquals(Arrays.asList(address2, address3), multisigAccountInfo.getMultisigAddresses());
  }

  @Test
  void isMultisigShouldReturnTrueWhenMinApprovalDifferentTo0() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 0, 1, Collections.emptyList(), Arrays.asList(address2, address3));

    assertFalse(multisigAccountInfo.isMultisig());
  }

  @Test
  void isMultisigShouldReturnTrueWhenMinRemovalDifferentTo0() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 1, 0, Collections.emptyList(), Arrays.asList(address2, address3));

    assertFalse(multisigAccountInfo.isMultisig());
  }

  @Test
  void isMultisigShouldReturnFalseWhenMinRemovalAndMinApprovalEqualsTo0() {
    MultisigAccountInfo multisigAccountInfo =
        new MultisigAccountInfo(
            "a", 1, address1, 1, 1, Collections.emptyList(), Arrays.asList(address2, address3));

    assertTrue(multisigAccountInfo.isMultisig());
  }

  @Test
  void serialize() {
    MultisigAccountInfo info =
        new MultisigAccountInfo(
            "a",
            1,
            address1,
            3,
            2,
            Arrays.asList(address3, address4),
            Arrays.asList(address2, address3));

    byte[] serializedState = info.serialize();
    String expectedHex =
        "010003000000020000009022D04812D05000F96C283657B0C17990932BC84926CDE6020000000000000090CF2886A23771534F2CEF86094B4C4FBC1E19C286B11E5B9049E14BEBCA93758EB36805BAE760A57239976F009A545C02000000000000009050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E190CF2886A23771534F2CEF86094B4C4FBC1E19C286B11E5B";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MultisigEntryBuilder builder =
        MultisigEntryBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(3, builder.getMinApproval());
    Assertions.assertEquals(2, builder.getMinRemoval());
  }
}
