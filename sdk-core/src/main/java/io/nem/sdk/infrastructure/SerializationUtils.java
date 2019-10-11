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

package io.nem.sdk.infrastructure;

import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;

public class SerializationUtils {


    public static PublicAccount toPublicAccount(KeyDto keyDto, NetworkType networkType) {
        String publicKey = ConvertUtils.toHex(keyDto.getKey().array());
        return PublicAccount.createFromPublicKey(publicKey, networkType);
    }

    public static Mosaic toMosaic(UnresolvedMosaicBuilder m) {
        return new Mosaic(
            new MosaicId(BigInteger.valueOf(m.getMosaicId().getUnresolvedMosaicId())),
            BigInteger.valueOf(m.getAmount().getAmount()));
    }


    public static int byteToUnsignedInt(byte b) {
        return b & 0xFF;
    }

    public static int shortToUnsignedInt(short b) {
        return b & 0xFFFF;
    }

}
