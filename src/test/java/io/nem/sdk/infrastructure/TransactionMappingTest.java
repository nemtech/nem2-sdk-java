/*
 * Copyright 2018 NEM
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.*;

import org.spongycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.stream.StreamSupport;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionMappingTest {

    @Test
    void shouldCreateStandaloneTransferTransaction() throws Exception {
        JsonObject transferTransactionDTO = new Gson().fromJson ("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[10000,0],\"fee\":[0,0],\"message\":{\"payload\":\"746573742D6D657373616765\",\"type\":0},\"mosaics\":[{\"amount\":[3863990592,95248],\"id\":[3646934825,3576016193]}],\"recipient\":\"9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142\",\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16724,\"version\":36867}}", JsonObject.class);

        Transaction transferTransaction = new TransactionMapping().apply(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
    }

    @Test
    void shouldCreateAggregateTransferTransaction() throws Exception {
        JsonObject aggregateTransferTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"message\":{\"payload\":\"746573742D6D657373616765\",\"type\":0},\"mosaics\":[{\"amount\":[3863990592,95248],\"id\":[3646934825,3576016193]}],\"recipient\":\"9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16724,\"version\":36867}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateTransferTransaction = new TransactionMapping().apply(aggregateTransferTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneRootNamespaceCreationTransaction() throws Exception {
        JsonObject namespaceCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[1,0],\"duration\":[1000,0],\"fee\":[0,0],\"name\":\"a2p1mg\",\"namespaceId\":[437145074,4152736179],\"namespaceType\":0,\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16718,\"version\":36867}}", JsonObject.class);

        Transaction namespaceCreationTransaction = new TransactionMapping().apply(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction, namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateRootNamespaceCreationTransaction() throws Exception {
        JsonObject aggregateNamespaceCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"duration\":[1000,0],\"name\":\"a2p1mg\",\"namespaceId\":[437145074,4152736179],\"namespaceType\":0,\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16718,\"version\":36867,\"fee\":[0,0],\"deadline\":[3266625578,11],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\"}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateNamespaceCreationTransaction = new TransactionMapping().apply(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateNamespaceCreationTransaction, aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSubNamespaceCreationTransaction() throws Exception {
        JsonObject namespaceCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[1,0],\"fee\":[0,0],\"name\":\"0unius\",\"namespaceId\":[1970060410,3289875941],\"namespaceType\":1,\"parentId\":[3316183705,3829351378],\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16718,\"version\":36867}}", JsonObject.class);

        Transaction namespaceCreationTransaction = new TransactionMapping().apply(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction, namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSubNamespaceCreationTransaction() throws Exception {
        JsonObject aggregateNamespaceCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"name\":\"0unius\",\"namespaceId\":[1970060410,3289875941],\"namespaceType\":1,\"parentId\":[3316183705,3829351378],\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16718,\"version\":36867,\"fee\":[0,0],\"deadline\":[3266625578,11],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\"}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateNamespaceCreationTransaction = new TransactionMapping().apply(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateNamespaceCreationTransaction, aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicCreationTransaction() throws Exception {
        JsonObject mosaicCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[1,0],\"fee\":[0,0],\"mosaicId\":[3248159581,740240531],\"name\":\"ie7rfaqxiorum1jor\",\"parentId\":[3316183705,3829351378],\"properties\":[{\"id\":0,\"value\":[7,0]},{\"id\":1,\"value\":[6,0]},{\"id\":2,\"value\":[1000,0]}],\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16717,\"version\":36867}}", JsonObject.class);

        Transaction mosaicCreationTransaction = new TransactionMapping().apply(mosaicCreationTransactionDTO);

        validateStandaloneTransaction(mosaicCreationTransaction, mosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicCreationTransaction() throws Exception {
        JsonObject aggregateMosaicCreationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"mosaicId\":[3248159581,740240531],\"name\":\"ie7rfaqxiorum1jor\",\"parentId\":[3316183705,3829351378],\"properties\":[{\"id\":0,\"value\":[7,0]},{\"id\":1,\"value\":[6,0]},{\"id\":2,\"value\":[1000,0]}],\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16717,\"version\":36867}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateMosaicCreationTransaction = new TransactionMapping().apply(aggregateMosaicCreationTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateMosaicCreationTransaction, aggregateMosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicSupplyChangeTransaction() throws Exception {
        JsonObject mosaicSupplyChangeTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[1,0],\"delta\":[100000,0],\"direction\":1,\"fee\":[0,0],\"mosaicId\":[3070467832,2688515262],\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16973,\"version\":36867}}", JsonObject.class);

        Transaction mosaicSupplyChangeTransaction = new TransactionMapping().apply(mosaicSupplyChangeTransactionDTO);

        validateStandaloneTransaction(mosaicSupplyChangeTransaction, mosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicSupplyChangeTransaction() throws Exception {
        JsonObject aggregateMosaicSupplyChangeTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"delta\":[100000,0],\"direction\":1,\"mosaicId\":[3070467832,2688515262],\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16973,\"version\":36867}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateMosaicSupplyChangeTransaction = new TransactionMapping().apply(aggregateMosaicSupplyChangeTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateMosaicSupplyChangeTransaction, aggregateMosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMultisigModificationTransaction() throws Exception {
        JsonObject multisigModificationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\",\"height\":[1,0],\"id\":\"59FDA0733F17CF0001772CA7\",\"index\":19,\"merkleComponentHash\":\"18C036C20B32348D63684E09A13128A2C18F6A75650D3A5FB43853D716E5E219\"},\"transaction\":{\"deadline\":[1,0],\"fee\":[0,0],\"minApprovalDelta\":1,\"minRemovalDelta\":1,\"modifications\":[{\"cosignatoryPublicKey\":\"589B73FBC22063E9AE6FBAC67CB9C6EA865EF556E5FB8B7310D45F77C1250B97\",\"type\":0}],\"signature\":\"553E696EB4A54E43A11D180EBA57E4B89D0048C9DD2604A9E0608120018B9E02F6EE63025FEEBCED3293B622AF8581334D0BDAB7541A9E7411E7EE4EF0BC5D0E\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16725,\"version\":36867}}", JsonObject.class);

        Transaction multisigModificationTransaction = new TransactionMapping().apply(multisigModificationTransactionDTO);

        validateStandaloneTransaction(multisigModificationTransaction, multisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMultisigModificationTransaction() throws Exception {
        JsonObject aggregateMultisigModificationTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"minApprovalDelta\":1,\"minRemovalDelta\":1,\"modifications\":[{\"cosignatoryPublicKey\":\"589B73FBC22063E9AE6FBAC67CB9C6EA865EF556E5FB8B7310D45F77C1250B97\",\"type\":0}],\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16725,\"version\":36867}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        Transaction aggregateMultisigModificationTransaction = new TransactionMapping().apply(aggregateMultisigModificationTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateMultisigModificationTransaction, aggregateMultisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneLockFundsTransaction() throws Exception {
        JsonObject lockFundsTransactionDTO = new Gson().fromJson("{\"meta\": {\"height\": [22115,0],\"hash\": \"796602E7AA17E1BECD6A0302AD18CC4AE9CB8B2C5DF4EE602C80F0A98120238D\",\"merkleComponentHash\": \"796602E7AA17E1BECD6A0302AD18CC4AE9CB8B2C5DF4EE602C80F0A98120238D\",\"index\": 0,\"id\": \"5A86F7FF5F8AE10001776B6C\"},\"transaction\": {\"signature\": \"298C9BB956C318431FD7BE912480DE57B0A997820A8F85DA824A5A0B81B63E8A58AB31936B371A6B500E0CBDE59C00A56B62F127EAA3E2BE3DF6F5C27FD3BD07\",\"signer\": \"1026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755\",\"version\": 36867,\"type\": 16716,\"fee\": [0,0],\"deadline\": [3498561481,13],\"duration\": [100,0],\"mosaicId\": [3646934825,3576016193],\"amount\": [10000000,0],\"hash\": \"49E9F58867FB9399F32316B99CCBC301A5790E5E0605E25F127D28CEF99740A3\"}}", JsonObject.class);

        Transaction lockFundsTransaction = new TransactionMapping().apply(lockFundsTransactionDTO);

        validateStandaloneTransaction(lockFundsTransaction, lockFundsTransactionDTO);
    }

    @Test
    void shouldCreateAggregateLockFundsTransaction() throws Exception {
        JsonObject aggregateLockFundsTransactionDTO = new Gson().fromJson("{\"meta\": {\"hash\": \"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E55\",\"index\": 0,\"merkleComponentHash\": \"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\": {\"cosignatures\": [{\"signature\": \"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\": \"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\": [3266625578,11],\"fee\": [0,0],\"signature\": \"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\": \"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\": [{\"meta\": {\"aggregateHash\": \"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\": \"5A0069D83F17CF0001777E55\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E56\",\"index\": 0},\"transaction\": {\"signer\": \"1026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755\",\"version\": 36867,\"type\": 16716,\"duration\": [100,0],\"mosaicId\": [3646934825,3576016193],\"amount\": [10000000,0],\"hash\": \"49E9F58867FB9399F32316B99CCBC301A5790E5E0605E25F127D28CEF99740A3\"}}],\"type\": 16705,\"version\": 36867}}", JsonObject.class);

        Transaction lockFundsTransaction = new TransactionMapping().apply(aggregateLockFundsTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) lockFundsTransaction, aggregateLockFundsTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretLockTransaction() throws Exception {
        JsonObject secretLockTransactionDTO = new Gson().fromJson("{\"meta\": {\"height\": [22211,0],\"hash\": \"B802E29269DC8DF68B63D8C802092D51854C42253E6F8083AE3304C17C0BEAF3\",\"merkleComponentHash\": \"B802E29269DC8DF68B63D8C802092D51854C42253E6F8083AE3304C17C0BEAF3\",\"index\": 0,\"id\": \"5A86FDCE5F8AE10001776BCF\"},\"transaction\": {\"signature\": \"9D66CA66BE5D02775A6ACD8913DC39D422FD60D36F1E67CEDE8B8615AD3258B2B1C9DBABA13208F571F2DD10C70B76DB6963E9BA237AC5281C2E2549B1F2D602\",\"signer\": \"846B4439154579A5903B1459C9CF69CB8153F6D0110A7A0ED61DE29AE4810BF2\",\"version\": 36867,\"type\": 16972,\"fee\": [0,0],\"deadline\": [3496454111,13],\"duration\": [100,0],\"mosaicId\": [3646934825,3576016193],\"amount\": [10000000,0],\"hashAlgorithm\": 0,\"secret\": \"428A9DEB1DC6B938AD7C83617E4A558D5316489ADE176AE0C821568A2AD6F700470901532716F83D43F2A7240FBB2C34BDD9536BCF6CC7601904782C385CD8B4\",\"recipient\": \"90C9B099BAEBB743A4D2D8D3B1520F6DD0A0E9D6C9D968C155\"}}", JsonObject.class);

        Transaction secretLockTransaction = new TransactionMapping().apply(secretLockTransactionDTO);

        validateStandaloneTransaction(secretLockTransaction, secretLockTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretLockTransaction() throws Exception {
        JsonObject aggregateSecretLockTransactionDTO = new Gson().fromJson("{\"meta\": {\"hash\": \"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E55\",\"index\": 0,\"merkleComponentHash\": \"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\": {\"cosignatures\": [{\"signature\": \"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\": \"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\": [3266625578,11],\"fee\": [0,0],\"signature\": \"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\": \"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\": [{\"meta\": {\"aggregateHash\": \"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\": \"5A0069D83F17CF0001777E55\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E56\",\"index\": 0},\"transaction\": {\"signer\": \"846B4439154579A5903B1459C9CF69CB8153F6D0110A7A0ED61DE29AE4810BF2\",\"version\": 36867,\"type\": 16972,\"duration\": [100,0],\"mosaicId\": [3646934825,3576016193],\"amount\": [10000000,0],\"hashAlgorithm\": 0,\"secret\": \"428A9DEB1DC6B938AD7C83617E4A558D5316489ADE176AE0C821568A2AD6F700470901532716F83D43F2A7240FBB2C34BDD9536BCF6CC7601904782C385CD8B4\",\"recipient\": \"90C9B099BAEBB743A4D2D8D3B1520F6DD0A0E9D6C9D968C155\"}}],\"type\": 16705,\"version\": 36867}}", JsonObject.class);

        Transaction aggregateSecretLockTransaction = new TransactionMapping().apply(aggregateSecretLockTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretLockTransaction, aggregateSecretLockTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretProofTransaction() throws Exception {
        JsonObject secretProofTransactionDTO = new Gson().fromJson("{\"meta\": {\"height\": [22212,0],\"hash\": \"A1BBEF9DF8F5170B43AFBB38BCA9140F38C7234C6F4AB306458F6AF2E2F0234A\",\"merkleComponentHash\": \"A1BBEF9DF8F5170B43AFBB38BCA9140F38C7234C6F4AB306458F6AF2E2F0234A\",\"index\": 0,\"id\": \"5A86FDDA5F8AE10001776BD2\"},\"transaction\": {\"signature\": \"7C52EA06C71843FD6B1AE30A04FECD53C0B78FE8A8A2925D96FE528401255CBB3F3156C99F2F3E4DEF01CD38A014B677AD4DB78733929C0C96BC28FD7D508D05\",\"signer\": \"74A6BD39F42535AA3608924A517A88E3B2C36B2DFC296CB379604A3FEE01C7B8\",\"version\": 36867,\"type\": 17228,\"fee\": [0,0],\"deadline\": [3496462687,13],\"hashAlgorithm\": 0,\"secret\": \"428A9DEB1DC6B938AD7C83617E4A558D5316489ADE176AE0C821568A2AD6F700470901532716F83D43F2A7240FBB2C34BDD9536BCF6CC7601904782C385CD8B4\",\"proof\": \"E08664BF179B064D9E3B\"}}", JsonObject.class);

        Transaction secretProofTransaction = new TransactionMapping().apply(secretProofTransactionDTO);

        validateStandaloneTransaction(secretProofTransaction, secretProofTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretProofTransaction() throws Exception {
        JsonObject aggregateSecretProofTransactionDTO = new Gson().fromJson("{\"meta\": {\"hash\": \"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E55\",\"index\": 0,\"merkleComponentHash\": \"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\": {\"cosignatures\": [{\"signature\": \"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\": \"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\": [3266625578,11],\"fee\": [0,0],\"signature\": \"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\": \"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\": [{\"meta\": {\"aggregateHash\": \"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\": \"5A0069D83F17CF0001777E55\",\"height\": [18160,0],\"id\": \"5A0069D83F17CF0001777E56\",\"index\": 0},\"transaction\": {\"signer\": \"74A6BD39F42535AA3608924A517A88E3B2C36B2DFC296CB379604A3FEE01C7B8\",\"version\": 36867,\"type\": 17228,\"hashAlgorithm\": 0,\"secret\": \"428A9DEB1DC6B938AD7C83617E4A558D5316489ADE176AE0C821568A2AD6F700470901532716F83D43F2A7240FBB2C34BDD9536BCF6CC7601904782C385CD8B4\",\"proof\": \"E08664BF179B064D9E3B\"}}],\"type\": 16705,\"version\": 36867}}", JsonObject.class);

        Transaction aggregateSecretProofTransaction = new TransactionMapping().apply(aggregateSecretProofTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretProofTransaction, aggregateSecretProofTransactionDTO);
    }

    void validateStandaloneTransaction(Transaction transaction, JsonObject transactionDTO) {
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("meta").getAsJsonArray("height")),
                transaction.getTransactionInfo().get().getHeight());
        if (transaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(transactionDTO.getAsJsonObject("meta").get("hash").getAsString(),
                    transaction.getTransactionInfo().get().getHash().get());
        }
        if (transaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(transactionDTO.getAsJsonObject("meta").get("merkleComponentHash").getAsString(),
                    transaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (transaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(transaction.getTransactionInfo().get().getIndex().get().intValue(),
                    transactionDTO.getAsJsonObject("meta").get("index").getAsInt());
        }
        if (transaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(transactionDTO.getAsJsonObject("meta").get("id").getAsString(),
                    transaction.getTransactionInfo().get().getId().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateHash().isPresent()) {
            assertEquals(transactionDTO.getAsJsonObject("meta").get("aggregateHash").getAsString(),
                    transaction.getTransactionInfo().get().getAggregateHash().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateId().isPresent()) {
            assertEquals(transactionDTO.getAsJsonObject("meta").get("aggregateId").getAsString(),
                    transaction.getTransactionInfo().get().getAggregateId().get());
        }

        assertEquals(transactionDTO.getAsJsonObject("transaction").get("signature").getAsString(), transaction.getSignature().get());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("signer").getAsString(), transaction.getSigner().get().getPublicKey());
        assertTrue(transaction.getType().getValue() == transactionDTO.getAsJsonObject("transaction").get("type").getAsInt());
        int version = (int) Long.parseLong(Integer.toHexString(transactionDTO.getAsJsonObject("transaction").get("version").getAsInt()).substring(2, 4), 16);
        assertTrue(transaction.getVersion() == version);
        int networkType = (int) Long.parseLong(Integer.toHexString(transactionDTO.getAsJsonObject("transaction").get("version").getAsInt()).substring(0, 2), 16);
        assertTrue(transaction.getNetworkType().getValue() == networkType);
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("fee")),
                transaction.getFee());
        assertNotNull(transaction.getDeadline());

        if (transaction.getType() == TransactionType.TRANSFER) {
            validateTransferTx((TransferTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.REGISTER_NAMESPACE) {
            validateNamespaceCreationTx((RegisterNamespaceTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_DEFINITION) {
            validateMosaicCreationTx((MosaicDefinitionTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            validateMosaicSupplyChangeTx((MosaicSupplyChangeTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MODIFY_MULTISIG_ACCOUNT) {
            validateMultisigModificationTx((ModifyMultisigAccountTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.LOCK) {
            validateLockFundsTx((LockFundsTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_LOCK) {
            validateSecretLockTx((SecretLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_PROOF) {
            validateSecretProofTx((SecretProofTransaction) transaction, transactionDTO);
        }

    }

    void validateAggregateTransaction(AggregateTransaction aggregateTransaction, JsonObject aggregateTransactionDTO) {
        assertEquals(extractBigInteger(aggregateTransactionDTO.getAsJsonObject("meta").getAsJsonArray("height")),
                aggregateTransaction.getTransactionInfo().get().getHeight());
        if (aggregateTransaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(aggregateTransactionDTO.getAsJsonObject("meta").get("hash").getAsString(),
                    aggregateTransaction.getTransactionInfo().get().getHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(aggregateTransactionDTO.getAsJsonObject("meta").get("merkleComponentHash").getAsString(),
                    aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(aggregateTransaction.getTransactionInfo().get().getIndex().get().intValue(),
                    aggregateTransactionDTO.getAsJsonObject("meta").get("index").getAsInt());
        }
        if (aggregateTransaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(aggregateTransactionDTO.getAsJsonObject("meta").get("id").getAsString(),
                    aggregateTransaction.getTransactionInfo().get().getId().get());
        }

        assertEquals(aggregateTransactionDTO.getAsJsonObject("transaction").get("signature").getAsString(), aggregateTransaction.getSignature().get());
        assertEquals(aggregateTransactionDTO.getAsJsonObject("transaction").get("signer").getAsString(), aggregateTransaction.getSigner().get().getPublicKey());
        int version = (int) Long.parseLong(Integer.toHexString(aggregateTransactionDTO.getAsJsonObject("transaction").get("version").getAsInt()).substring(2, 4), 16);
        assertTrue(aggregateTransaction.getVersion() == version);
        int networkType = (int) Long.parseLong(Integer.toHexString(aggregateTransactionDTO.getAsJsonObject("transaction").get("version").getAsInt()).substring(0, 2), 16);
        assertTrue(aggregateTransaction.getNetworkType().getValue() == networkType);
        assertTrue(aggregateTransaction.getType().getValue() == aggregateTransactionDTO.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(extractBigInteger(aggregateTransactionDTO.getAsJsonObject("transaction").getAsJsonArray("fee")),
                aggregateTransaction.getFee());
        assertNotNull(aggregateTransaction.getDeadline());


        assertEquals(aggregateTransactionDTO.getAsJsonObject("transaction").getAsJsonArray("cosignatures").get(0).getAsJsonObject().get("signature").getAsString(),
                aggregateTransaction.getCosignatures().get(0).getSignature());
        assertEquals(aggregateTransactionDTO.getAsJsonObject("transaction").getAsJsonArray("cosignatures").get(0).getAsJsonObject().get("signer").getAsString(),
                aggregateTransaction.getCosignatures().get(0).getSigner().getPublicKey());

        Transaction innerTransaction = aggregateTransaction.getInnerTransactions().get(0);
        validateStandaloneTransaction(innerTransaction, aggregateTransactionDTO.getAsJsonObject("transaction").getAsJsonArray("transactions").get(0).getAsJsonObject());
    }

    void validateTransferTx(TransferTransaction transaction, JsonObject transactionDTO) {
        assertEquals(Address.createFromEncoded(transactionDTO.getAsJsonObject("transaction").get("recipient").getAsString()), transaction.getRecipient());

        JsonArray mosaicsDTO = transactionDTO.getAsJsonObject("transaction").getAsJsonArray("mosaics");
        if (mosaicsDTO != null && mosaicsDTO.size() > 0) {
            assertEquals(extractBigInteger(mosaicsDTO.get(0).getAsJsonObject().getAsJsonArray("id")),
                    transaction.getMosaics().get(0).getId().getId());
            assertEquals(extractBigInteger(mosaicsDTO.get(0).getAsJsonObject().getAsJsonArray("amount")),
                    transaction.getMosaics().get(0).getAmount());
        }

        try {
            assertEquals(new String(Hex.decode(transactionDTO.getAsJsonObject("transaction").getAsJsonObject("message").get("payload").getAsString()), "UTF-8"),
                    transaction.getMessage().getPayload());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assertTrue(transactionDTO.getAsJsonObject("transaction").getAsJsonObject("message").get("type").getAsInt() ==
                transaction.getMessage().getType());

    }

    void validateNamespaceCreationTx(RegisterNamespaceTransaction transaction, JsonObject transactionDTO) {
        assertTrue(transactionDTO.getAsJsonObject("transaction").get("namespaceType").getAsInt() ==
                transaction.getNamespaceType().getValue());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("name").getAsString(),
                transaction.getNamespaceName());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("namespaceId")),
                transaction.getNamespaceId().getId());

        if (transaction.getNamespaceType() == NamespaceType.RootNamespace) {
            assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("duration")),
                    transaction.getDuration().get());
        } else {
            assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("parentId")),
                    transaction.getParentId().get().getId());
        }
    }

    void validateMosaicCreationTx(MosaicDefinitionTransaction transaction, JsonObject transactionDTO) {
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("parentId")),
                transaction.getNamespaceId().getId());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("mosaicId")),
                transaction.getMosaicId().getId());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("name").getAsString(),
                transaction.getMosaicName());
        assertTrue(transaction.getMosaicProperties().getDivisibility() ==
                transactionDTO.getAsJsonObject("transaction").getAsJsonArray("properties").get(1).getAsJsonObject().get("value").getAsJsonArray().get(0).getAsInt());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("properties").get(2).getAsJsonObject().get("value").getAsJsonArray()),
                transaction.getMosaicProperties().getDuration());
        assertTrue(transaction.getMosaicProperties().isSupplyMutable());
        assertTrue(transaction.getMosaicProperties().isTransferable());
        assertTrue(transaction.getMosaicProperties().isLevyMutable());
    }

    void validateMosaicSupplyChangeTx(MosaicSupplyChangeTransaction transaction, JsonObject transactionDTO) {
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("mosaicId")),
                transaction.getMosaicId().getId());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("delta")),
                transaction.getDelta());
        assertTrue(transaction.getMosaicSupplyType().getValue() == transactionDTO.getAsJsonObject("transaction").get("direction").getAsInt());
    }

    void validateMultisigModificationTx(ModifyMultisigAccountTransaction transaction, JsonObject transactionDTO) {
        assertTrue(transaction.getMinApprovalDelta() == transactionDTO.getAsJsonObject("transaction").get("minApprovalDelta").getAsInt());
        assertTrue(transaction.getMinRemovalDelta() == transactionDTO.getAsJsonObject("transaction").get("minRemovalDelta").getAsInt());
        assertEquals(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("modifications").get(0).getAsJsonObject().get("cosignatoryPublicKey").getAsString(),
                transaction.getModifications().get(0).getCosignatoryPublicAccount().getPublicKey());
        assertTrue(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("modifications").get(0).getAsJsonObject().get("type").getAsInt() ==
                transaction.getModifications().get(0).getType().getValue());
    }

    void validateLockFundsTx(LockFundsTransaction transaction, JsonObject transactionDTO) {
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("mosaicId")),
                transaction.getMosaic().getId().getId());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("amount")),
                transaction.getMosaic().getAmount());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("duration")),
                transaction.getDuration());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("hash").getAsString(),
                transaction.getSignedTransaction().getHash());
    }

    void validateSecretLockTx(SecretLockTransaction transaction, JsonObject transactionDTO) {
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("mosaicId")),
                transaction.getMosaic().getId().getId());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("amount")),
                transaction.getMosaic().getAmount());
        assertEquals(extractBigInteger(transactionDTO.getAsJsonObject("transaction").getAsJsonArray("duration")),
                transaction.getDuration());
        assertTrue(transactionDTO.getAsJsonObject("transaction").get("hashAlgorithm").getAsInt() ==
                transaction.getHashType().getValue());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("secret").getAsString(),
                transaction.getSecret());
        assertEquals(Address.createFromEncoded(transactionDTO.getAsJsonObject("transaction").get("recipient").getAsString()), transaction.getRecipient());
    }

    void validateSecretProofTx(SecretProofTransaction transaction, JsonObject transactionDTO) {
        assertTrue(transactionDTO.getAsJsonObject("transaction").get("hashAlgorithm").getAsInt() ==
                transaction.getHashType().getValue());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("secret").getAsString(),
                transaction.getSecret());
        assertEquals(transactionDTO.getAsJsonObject("transaction").get("proof").getAsString(),
                transaction.getProof());
    }

    BigInteger extractBigInteger(JsonArray input) {
        UInt64DTO uInt64DTO = new UInt64DTO();
        StreamSupport.stream(input.spliterator(), false).forEach(item -> uInt64DTO.add(new Long(item.toString())));
        return uInt64DTO.extractIntArray();
    }
}
