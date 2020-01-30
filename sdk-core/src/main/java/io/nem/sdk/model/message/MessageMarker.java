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

package io.nem.sdk.model.message;

/**
 * Special message prefix for catapult.
 */
public class MessageMarker {

    /**
     * private constructor.
     */
    private MessageMarker(){

    }
    /**
     * 8-byte marker: FE 2A 80 61 57 73 01 E2 for PersistentDelegationRequestTransaction message
     */
    public static final String PERSISTENT_DELEGATION_UNLOCK = "FE2A8061577301E2";
}
