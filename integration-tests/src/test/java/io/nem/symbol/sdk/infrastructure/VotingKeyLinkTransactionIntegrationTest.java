package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransactionFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VotingKeyLinkTransactionIntegrationTest extends BaseIntegrationTest {

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void basicAnnounce(RepositoryType type) {

        Account account = config().getNemesisAccount2();
        VotingKey linkedPublicKey = VotingKey.fromHexString("AAAAA");

        VotingKeyLinkTransaction linkTransaction = VotingKeyLinkTransactionFactory
            .create(getNetworkType(), linkedPublicKey, BigInteger.valueOf(72), BigInteger.valueOf(26280),
                LinkAction.LINK)
            .maxFee(this.maxFee).build();

        announceAndValidate(type, account, linkTransaction);

        VotingKeyLinkTransaction unlinkTransaction = VotingKeyLinkTransactionFactory
            .create(getNetworkType(), linkedPublicKey, BigInteger.valueOf(72), BigInteger.valueOf(26280),
                LinkAction.UNLINK)
            .maxFee(this.maxFee).build();

        announceAndValidate(type, account, unlinkTransaction);
    }
}
