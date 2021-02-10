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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Config {

  private static final String CONFIG_JSON = "./integration-tests/src/test/resources/config.json";
  private static final String ADDRESSES_YML = "./target/bootstrap/addresses.yml";
  //  private static final String ADDRESSES_YML =
  //        "../symbol-bootstrap/target/bootstrap/addresses.yml";
  //  private static final String ADDRESSES_YML = "../catapult-rest/rest/target/addresses.yml";

  private final JsonObject config;
  private final Map<String, Account> accountCache = new HashMap<>();
  private List<Account> nemesisAccounts;
  private NetworkType networkType;

  public Config() {

    try (InputStream inputStream = getConfigInputStream()) {
      this.config = new JsonObject(IOUtils.toString(inputStream));
    } catch (IOException e) {
      throw new IllegalStateException(
          "Config file could not be loaded. " + ExceptionUtils.getMessage(e), e);
    }
  }

  private static List<Account> loadNemesisAccountsFromBootstrap(
      NetworkType networkType, File generatedAddresses) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      if (!generatedAddresses.exists()) {
        System.out.println(
            "Generated addresses could not be found in "
                + generatedAddresses.getAbsolutePath()
                + " Nemesis address must bue added manually");
        return Collections.emptyList();
      }

      List<Map<String, String>> bootstrapAddresses;
      List<Map<String, List<Map<String, String>>>> mosaics =
          (List<Map<String, List<Map<String, String>>>>)
              mapper.readValue(generatedAddresses, Map.class).get("mosaics");
      bootstrapAddresses = mosaics.get(0).get("accounts");

      return bootstrapAddresses.stream()
          .map(m -> Account.createFromPrivateKey(m.get("privateKey"), networkType))
          .collect(Collectors.toList());

    } catch (Exception e) {
      throw new IllegalStateException(
          "Nemesis account could not be loaded from Bootstrap: " + ExceptionUtils.getMessage(e), e);
    }
  }

  private static List<Account> loadNemesisAccountsFromBootstrap(NetworkType networkType) {
    return loadNemesisAccountsFromBootstrap(networkType, loadConfigFile(ADDRESSES_YML));
  }

  private static InputStream getConfigInputStream() throws IOException {
    return new FileInputStream(loadConfigFile(CONFIG_JSON));
  }

  private static File loadConfigFile(String configFile) {

    File file = new File(configFile);
    if (!file.exists()) {
      file = new File("." + configFile);
      if (!file.exists()) {
        file = new File("../" + configFile);
        if (!file.exists()) {
          throw new IllegalArgumentException("File " + file.getAbsolutePath() + " doesn't exist");
        }
      }
    }
    if (file.isDirectory()) {
      throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is a directory!");
    }
    return file;
  }

  public void init(NetworkType networkType) {
    this.networkType = networkType;
    this.nemesisAccounts = loadNemesisAccountsFromBootstrap(getNetworkType());
  }

  public String getApiUrl() {
    return this.config.getString("apiUrl");
  }

  public NetworkType getNetworkType() {
    return networkType;
  }

  public Long getTimeoutSeconds() {
    return this.config.getLong("timeoutSeconds");
  }

  public Account getMultisigAccount() {
    return getAccount("multisigAccount");
  }

  public Account getCosignatoryAccount() {
    return getAccount("cosignatoryAccount");
  }

  public Account getCosignatory2Account() {
    return getAccount("cosignatory2Account");
  }

  public Account getHarvestingAccount() {
    return getAccount("harvestingAccount");
  }

  public Account getDefaultAccount() {
    // TODO - Replace with getTestAccount once it doesn't run out of currency.
    return getNemesisAccount1();
  }

  public Account getNemesisAccount(int index) {
    return getOptionalAccount("nemesisAccount")
        .orElseGet(
            () ->
                getNemesisAccounts().stream()
                    .skip(index)
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                "No nemesis account could not be found at index " + index)));
  }

  public List<Account> getNemesisAccounts() {
    return nemesisAccounts;
  }

  public Account getNemesisAccount() {
    return getNemesisAccount(0);
  }

  public Account getNemesisAccount1() {
    return getNemesisAccount(0);
  }

  public Account getNemesisAccount2() {
    return getNemesisAccount(1);
  }

  public Account getNemesisAccount3() {
    return getNemesisAccount(2);
  }

  public Account getNemesisAccount4() {
    return getNemesisAccount(3);
  }

  public Account getNemesisAccount5() {
    return getNemesisAccount(4);
  }

  public Account getNemesisAccount6() {
    return getNemesisAccount(5);
  }

  public Account getNemesisAccount7() {
    return getNemesisAccount(6);
  }

  public Account getNemesisAccount8() {
    return getNemesisAccount(7);
  }

  public Account getNemesisAccount9() {
    return getNemesisAccount(8);
  }

  public Account getNemesisAccount10() {
    return getNemesisAccount(9);
  }

  public Account getTestAccount() {
    return getAccount("testAccount");
  }

  public Account getTestAccount2() {
    return getAccount("testAccount2");
  }

  public Account getTestAccount3() {
    return getAccount("testAccount3");
  }

  public Account getCosignatory3Account() {
    return getAccount("cosignatory3Account");
  }

  private Account getAccount(String accountName) {
    return getOptionalAccount(accountName)
        .orElseThrow(
            () -> new IllegalArgumentException(accountName + " account could not be found"));
  }

  private Optional<Account> getOptionalAccount(String accountName) {
    if (this.config.containsKey(accountName)) {
      return Optional.of(
          accountCache.computeIfAbsent(
              accountName,
              key ->
                  Account.createFromPrivateKey(
                      this.config.getJsonObject(accountName).getString("privateKey"),
                      getNetworkType())));
    } else {
      return Optional.empty();
    }
  }

  public Deadline getDeadline() {
    return null;
  }
}
