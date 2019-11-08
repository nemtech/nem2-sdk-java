package io.nem.sdk.model.transaction;


import io.nem.catapult.builders.AccountRestrictionFlagsDto;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AccountRestrictionType}
 */
public class AccountRestrictionTypeTest {


    @Test
    public void shouldMatchCatbufferValues() {
        Arrays.stream(AccountRestrictionType.values()).forEach(a -> {
            //Testing that all possible values are handled.
            Assertions.assertNotNull(AccountRestrictionFlagsDto.rawValueOf((byte) a.getValue()));
        });
    }

}
