package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.transaction.AccountRestrictionFlags;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountRestrictionsTest {

    @Test
    void shouldCreateAccountRestrictionsViaConstructor() {
        Address address =
            Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        AccountRestriction accountRestriction =
            new AccountRestriction(
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountRestrictions accountRestrictions =
            new AccountRestrictions(address, Arrays.asList(accountRestriction));

        assertEquals(address, accountRestrictions.getAddress());
        assertEquals(1, accountRestrictions.getRestrictions().size());
        assertEquals(
            AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
    }
}
