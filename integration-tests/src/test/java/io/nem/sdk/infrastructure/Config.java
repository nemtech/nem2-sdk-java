package io.nem.sdk.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
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
    private JsonObject config;
    private List<Account> nemesisAccounts;
    private Map<String, Account> accountCache = new HashMap<>();
    private NetworkType networkType;

    public Config() {

        try (InputStream inputStream = getConfigInputStream()) {
            if (inputStream == null) {
                throw new IOException(CONFIG_JSON + " not found");
            }
            this.config = new JsonObject(IOUtils.toString(inputStream));
        } catch (IOException e) {
            throw new IllegalStateException(
                "Config file could not be loaded. " + ExceptionUtils.getMessage(e), e);
        }
    }

    public void init(NetworkType networkType) {
        this.networkType = networkType;
        this.nemesisAccounts = loadNemesisAccountsFromBootstrap(getNetworkType());
    }

    private static List<Account> loadNemesisAccountsFromBootstrap(NetworkType networkType) {
        String homeFolder = System.getProperty("user.home");

        String bootstrapFolder = System.getenv("CATAPULT_SERVICE_BOOTSTRAP");

        if (StringUtils.isNotBlank(bootstrapFolder)) {
            File generatedAddressesOption = new File(
                StringUtils.removeEnd(bootstrapFolder, "/")
                    + "/build/generated-addresses/addresses.yaml");
            return loadNemesisAccountsFromBootstrap(networkType, generatedAddressesOption);
        }
        File generatedAddressesOption = new File(
            homeFolder
                + "/develop/workspace-nem/catapult-service-bootstrap/build/generated-addresses/addresses.yaml");
        return loadNemesisAccountsFromBootstrap(networkType, generatedAddressesOption);
    }

    private static List<Account> loadNemesisAccountsFromBootstrap(NetworkType networkType,
        File generatedAddresses) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            if (!generatedAddresses.exists()) {
                System.out.println("Generated addresses could not be found in " + generatedAddresses
                    .getAbsolutePath()
                    + " Nemesis address must bue added manually");
                return Collections.emptyList();
            }
            List<Map<String, String>> bootstrapAddresses = (List<Map<String, String>>) mapper
                .readValue(
                    generatedAddresses,
                    Map.class).get("nemesis_addresses");

            return bootstrapAddresses.stream()
                .map(m -> Account.createFromPrivateKey(m.get("private"), networkType)).collect(
                    Collectors.toList());

        } catch (Exception e) {
            System.err
                .println("Nemesis account could not be loaded from Bootstrap: " + ExceptionUtils
                    .getMessage(e));
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static InputStream getConfigInputStream() throws IOException {
        String cwd = System.getProperty("user.home");
        File localConfiguration = new File(new File(cwd),
            "nem-sdk-java-integration-test-config.json");
        if (localConfiguration.exists()) {
            System.out.println("Using local configuration " + localConfiguration);
            return new FileInputStream(localConfiguration);
        } else {
            System.out.println("Local configuration " + localConfiguration.getPath()
                + " not found. Using shared config.json");
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
        return getNemesisAccount();
    }

    public Account getNemesisAccount() {
        return getOptionalAccount("nemesisAccount")
            .orElseGet(() -> nemesisAccounts.stream().findFirst().orElseThrow(
                () -> new IllegalArgumentException("No nemesis account could not be found")));
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
        return getOptionalAccount(accountName).orElseThrow(
            () -> new IllegalArgumentException(accountName + " account could not be found"));
    }

    private Optional<Account> getOptionalAccount(String accountName) {
        if (this.config.containsKey(accountName)) {
            return Optional
                .of(accountCache.computeIfAbsent(accountName, key -> Account.createFromPrivateKey(
                    this.config.getJsonObject(accountName).getString("privateKey"),
                    getNetworkType())));
        } else {
            return Optional.empty();
        }

    }
}
