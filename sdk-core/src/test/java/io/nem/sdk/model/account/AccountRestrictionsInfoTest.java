package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.transaction.AccountRestrictionType;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AccountRestrictionsInfoTest {

    @Test
    void shouldCreateAccountRestrictionsInfoViaConstructor() {
        String metaId = "12345";
        Address address =
            Address.createFromEncoded("9050b9837efab4bbe8a4b9bb32d812f9885c00d8fc1650e142");
        AccountRestriction accountRestriction = new AccountRestriction(AccountRestrictionType.ALLOW_OUTGOING_ADDRESS,
            Collections.singletonList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountRestrictions accountRestrictions =
            new AccountRestrictions(address, Arrays.asList(accountRestriction));
        AccountPropertiesInfo accountPropertiesInfo =
            new AccountPropertiesInfo(metaId, accountRestrictions);

        assertEquals(metaId, accountPropertiesInfo.getMetaId());
        assertEquals(address, accountPropertiesInfo.getAccountRestrictions().getAddress());
        assertEquals(1, accountPropertiesInfo.getAccountRestrictions().getRestrictions().size());
        assertEquals(AccountRestrictionType.ALLOW_OUTGOING_ADDRESS,
            accountPropertiesInfo.getAccountRestrictions().getRestrictions().get(0).getRestrictionType());
    }
}
