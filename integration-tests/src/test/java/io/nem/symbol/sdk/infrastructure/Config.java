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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Config {

    private static final String CONFIG_JSON = "config.json";
    private final JsonObject config;
    private List<Account> nemesisAccounts;
    private final Map<String, Account> accountCache = new HashMap<>();
    private NetworkType networkType;

    public Config() {

        try (InputStream inputStream = getConfigInputStream()) {
            if (inputStream == null) {
                throw new IOException(CONFIG_JSON + " not found");
            }
            this.config = new JsonObject(IOUtils.toString(inputStream));
        } catch (IOException e) {
            throw new IllegalStateException("Config file could not be loaded. " + ExceptionUtils.getMessage(e), e);
        }
    }

    public void init(NetworkType networkType) {
        this.networkType = networkType;
        this.nemesisAccounts = loadNemesisAccountsFromBootstrap(getNetworkType());
    }

    private static List<Account> loadNemesisAccountsFromBootstrap(NetworkType networkType) {

        String bootstrapFolder = System.getenv("CATAPULT_SERVICE_BOOTSTRAP");
        if (StringUtils.isBlank(bootstrapFolder)) {
            bootstrapFolder = "../../catapult-service-bootstrap";
        }
        File generatedAddressesOption = new File(
            StringUtils.removeEnd(bootstrapFolder, "/") + "/build/generated-addresses/addresses.yaml");
        if (!generatedAddressesOption.exists()) {
            throw new IllegalArgumentException("File " + generatedAddressesOption.getAbsolutePath() + " doesn't exist");
        }
        if (generatedAddressesOption.isDirectory()) {
            throw new IllegalArgumentException(
                "File " + generatedAddressesOption.getAbsolutePath() + " is a directory!");
        }
        return loadNemesisAccountsFromBootstrap(networkType, generatedAddressesOption);

    }

    private static List<Account> loadNemesisAccountsFromBootstrap(NetworkType networkType, File generatedAddresses) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            if (!generatedAddresses.exists()) {
                System.out.println("Generated addresses could not be found in " + generatedAddresses.getAbsolutePath()
                    + " Nemesis address must bue added manually");
                return Collections.emptyList();
            }
            List<Map<String, String>> bootstrapAddresses = (List<Map<String, String>>) mapper
                .readValue(generatedAddresses, Map.class).get("nemesis_addresses");

            return bootstrapAddresses.stream().map(m -> Account.createFromPrivateKey(m.get("private"), networkType))
                .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Nemesis account could not be loaded from Bootstrap: " + ExceptionUtils.getMessage(e));
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static InputStream getConfigInputStream() throws IOException {
        String cwd = System.getProperty("user.home");
        File localConfiguration = new File(new File(cwd), "nem-sdk-java-integration-test-config.json");
        if (localConfiguration.exists()) {
            System.out.println("Using local configuration " + localConfiguration);
            return new FileInputStream(localConfiguration);
        } else {
            System.out.println(
                "Local configuration " + localConfiguration.getPath() + " not found. Using shared config.json");
            return BaseIntegrationTest.class.getClassLoader().getResourceAsStream(CONFIG_JSON);
        }
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
        //TODO - Replace with getTestAccount once it doesn't run out of currency.
        return getNemesisAccount1();
    }


    public Account getNemesisAccount(int index) {
        return getOptionalAccount("nemesisAccount").orElseGet(
            () -> getNemesisAccounts().stream().skip(index).findFirst().orElseThrow(
                () -> new IllegalArgumentException("No nemesis account could not be found at index " + index)));
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

    public Account getNemesisAccount11() {
        return getNemesisAccount(10);
    }

    public Account getNemesisAccount12() {
        return getNemesisAccount(11);
    }

    public Account getNemesisAccount13() {
        return getNemesisAccount(12);
    }

    public Account getNemesisAccount14() {
        return getNemesisAccount(13);
    }

    public Account getTestAccount() {
        return getAccount("testAccount");
    }

    public Account getTestAccount2() {
        return getAccount("testAccount2");
    }

    public Account getCosignatory3Account() {
        return getAccount("cosignatory3Account");
    }

    private Account getAccount(String accountName) {
        return getOptionalAccount(accountName)
            .orElseThrow(() -> new IllegalArgumentException(accountName + " account could not be found"));
    }

    private Optional<Account> getOptionalAccount(String accountName) {
        if (this.config.containsKey(accountName)) {
            return Optional.of(accountCache.computeIfAbsent(accountName, key -> Account
                .createFromPrivateKey(this.config.getJsonObject(accountName).getString("privateKey"),
                    getNetworkType())));
        } else {
            return Optional.empty();
        }

    }
}
