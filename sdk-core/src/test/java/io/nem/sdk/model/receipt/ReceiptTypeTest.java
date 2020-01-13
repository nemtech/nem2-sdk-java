/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.receipt;

import io.nem.catapult.builders.ReceiptTypeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ReceiptTypeTest {


    @ParameterizedTest
    @EnumSource(ReceiptType.class)
    void validReceiptTypeValues(ReceiptType transactionType) {

        Assertions.assertNotNull(ReceiptTypeDto.rawValueOf((short) transactionType.getValue()),
            transactionType.getValue() + " not found. ReceiptType " + transactionType.getValue());
    }

    @ParameterizedTest
    @EnumSource(ReceiptTypeDto.class)
    void valueReceiptTypeDtoValue(ReceiptTypeDto enumTypeDto) {
        if (enumTypeDto == ReceiptTypeDto.RESERVED) {
            return;
        }
        Assertions.assertNotNull(ReceiptTypeDto.rawValueOf(enumTypeDto.getValue()),
            enumTypeDto.getValue() + " not found. ReceiptType " + enumTypeDto.getValue());


    }
}
