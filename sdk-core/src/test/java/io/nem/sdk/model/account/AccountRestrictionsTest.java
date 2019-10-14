package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.transaction.AccountRestrictionType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountRestrictionsTest {

    @Test
    void shouldCreateAccountRestrictionsViaConstructor() {
        Address address =
            Address.createFromEncoded("9050b9837efab4bbe8a4b9bb32d812f9885c00d8fc1650e142");
        AccountRestriction accountRestriction =
            new AccountRestriction(
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountRestrictions accountRestrictions =
            new AccountRestrictions(address, Arrays.asList(accountRestriction));

        assertEquals(address, accountRestrictions.getAddress());
        assertEquals(1, accountRestrictions.getRestrictions().size());
        assertEquals(
            AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            accountRestrictions.getRestrictions().get(0).getRestrictionType());
    }
}
