package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AccountLinkTransactionTest {
    static Account account;

    @BeforeAll
    public static void setup() {
        account = new Account("041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }
    @Test
    void create() {
        AccountLinkTransaction accountLinkTransaction = AccountLinkTransaction.create(
                new FakeDeadline(),
                account.getPublicKey(),
                LinkActionType.LINK,
                NetworkType.MIJIN_TEST
        );
        assertEquals( accountLinkTransaction.getLinkAction(),LinkActionType.LINK);
        assertEquals(accountLinkTransaction.getRemoteAccountKey(),"9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24");
    }

}
