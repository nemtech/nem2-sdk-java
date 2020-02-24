/*
 * Copyright 2020 NEM
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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import java.util.List;

/**
 * The transfer transactions object contain data about transfers of mosaics and message to another
 * account.
 */
public class TransferTransaction extends Transaction {

    private final UnresolvedAddress recipient;
    private final List<Mosaic> mosaics;
    private final Message message;

    /**
     * Constructor of the transfer transaction using the factory.
     *
     * @param factory the factory;
     */
    TransferTransaction(TransferTransactionFactory factory) {
        super(factory);
        this.recipient = factory.getRecipient();
        this.mosaics = factory.getMosaics();
        this.message = factory.getMessage();
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public UnresolvedAddress getRecipient() {
        return recipient;
    }


    /**
     * Returns list of mosaic objects.
     *
     * @return List of {@link Mosaic}
     */
    public List<Mosaic> getMosaics() {
        return mosaics;
    }

    /**
     * Returns transaction message.
     *
     * @return Message.
     */
    public Message getMessage() {
        return message;
    }

}
