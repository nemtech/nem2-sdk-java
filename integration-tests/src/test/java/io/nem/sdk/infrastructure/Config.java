package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Config {

    private static final String CONFIG_JSON = "config.json";
    private static Config ourInstance = new Config();
    private JsonObject config;
    private Map<String, Account> accountCache = new HashMap<>();

    private Config() {

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

    public static Config getInstance() {
        return ourInstance;
    }

    public String getApiUrl() {
        return this.config.getString("apiUrl");
    }

    public NetworkType getNetworkType() {
        return NetworkType.valueOf(this.config.getString("networkType"));
    }

    public String getGenerationHash() {
        return this.config.getString("generationHash");
    }

    public Long getTimeoutSeconds() {
        return this.config.getLong("timeoutSeconds");
    }


    public String getTestAccountAddress() {
        return this.config.getJsonObject("testAccount").getString("address");
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

    public Account getNemesisAccount() {
        return getAccount("nemesisAccount");
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
        return accountCache.computeIfAbsent(accountName, key -> Account.createFromPrivateKey(
            this.config.getJsonObject(accountName).getString("privateKey"), getNetworkType()));

    }
}
