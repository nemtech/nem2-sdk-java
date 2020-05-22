package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransactionFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NodeKeyLinkTransactionIntegrationTest extends BaseIntegrationTest {

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void basicAnnounce(RepositoryType type) {

        Account account = config().getNemesisAccount2();
        PublicKey linkedPublicKey = PublicKey
            .fromHexString("F5D0AAD909CFBC810A3F888C33C57A9051AE1A59D1CDA872A8B90BCA7EF2D34A");

        NodeKeyLinkTransaction linkTransaction =
            NodeKeyLinkTransactionFactory.create(
                getNetworkType(),
                linkedPublicKey, LinkAction.LINK
            ).maxFee(this.maxFee).build();

        announceAndValidate(type, account, linkTransaction);

        NodeKeyLinkTransaction unlinkTransaction =
            NodeKeyLinkTransactionFactory.create(
                getNetworkType(),
                linkedPublicKey, LinkAction.UNLINK
            ).maxFee(this.maxFee).build();

        announceAndValidate(type, account, unlinkTransaction);
    }
}
