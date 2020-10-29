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
package io.nem.symbol.sdk.model.metadata;

import io.nem.symbol.catapult.builders.MetadataEntryBuilder;
import io.nem.symbol.catapult.builders.MetadataValueBuilder;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for metadata. */
public class MetadataTest {

  NetworkType networkType = NetworkType.MIJIN_TEST;
  PublicKey targetAccount =
      PublicKey.fromHexString("1111111111111111111111111111111111111111111111111111111111111111");
  PublicKey sourceAccount =
      PublicKey.fromHexString("2222222222222222222222222222222222222222222222222222222222222222");
  Address sourceAddress = Address.createFromPublicKey(sourceAccount.toHex(), networkType);
  Address targetAddress = Address.createFromPublicKey(targetAccount.toHex(), networkType);
  BigInteger metadataKey = BigInteger.valueOf(10);
  String value = "Some String";
  NamespaceId namespaceId = NamespaceId.createFromName("this.currency");
  MosaicId mosaicId = MosaicId.createFromNonce(new MosaicNonce(1), sourceAddress);
  String recordId = "someId";
  String compositeHash = "compositeHash";

  @Test
  public void testMetadataValueBuilder() {
    String plainText = "Some String";
    MetadataValueBuilder builder =
        MetadataValueBuilder.create(ByteBuffer.wrap(StringEncoder.getBytes(plainText)));
    Assertions.assertEquals(11, StringEncoder.getBytes(plainText).length);
    //    Assertions.assertEquals(11, builder.getStreamSize());
    Assertions.assertEquals(13, builder.getSize());
    Assertions.assertEquals(plainText, StringEncoder.getString(builder.getData().array()));

    MetadataValueBuilder deserializedBuilder =
        MetadataValueBuilder.loadFromBinary(SerializationUtils.toDataInput(builder.serialize()));

    Assertions.assertEquals(
        plainText, StringEncoder.getString(deserializedBuilder.getData().array()));
    Assertions.assertEquals(13, deserializedBuilder.getSize());
  }

  /** Contructor */
  @Test
  public void constructorNamespace() {
    Metadata metadata =
        new Metadata(
            recordId,
            compositeHash,
            sourceAddress,
            targetAddress,
            metadataKey,
            MetadataType.NAMESPACE,
            value,
            namespaceId.getIdAsHex());

    Assertions.assertEquals(namespaceId, metadata.getTargetId().get());
    Assertions.assertEquals(recordId, metadata.getRecordId().get());
    Assertions.assertEquals(compositeHash, metadata.getCompositeHash());
    Assertions.assertEquals(sourceAddress, metadata.getSourceAddress());
    Assertions.assertEquals(targetAddress, metadata.getTargetAddress());
    Assertions.assertEquals(MetadataType.NAMESPACE, metadata.getMetadataType());
    Assertions.assertEquals(value, metadata.getValue());

    byte[] serializedState = metadata.serialize();
    String expectedHex =
        "900E96DC85F6B24AC9C8DB5FFC59C35880C0B722C7A416A790FD35818960C7B18B72F49A5598FA9F712A354DB38EB0760A0000000000000068E0AE3A0168EDBD020B00536F6D6520537472696E67";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MetadataEntryBuilder builder =
        MetadataEntryBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(metadata.serialize()), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(value, StringEncoder.getString(builder.getValue().getData().array()));
  }

  /** Contructor */
  @Test
  public void constructorAccount() {
    Metadata metadata =
        new Metadata(
            recordId,
            compositeHash,
            sourceAddress,
            targetAddress,
            metadataKey,
            MetadataType.ACCOUNT,
            value,
            null);

    Assertions.assertEquals(recordId, metadata.getRecordId().get());
    Assertions.assertFalse(metadata.getTargetId().isPresent());
    Assertions.assertEquals(compositeHash, metadata.getCompositeHash());
    Assertions.assertEquals(sourceAddress, metadata.getSourceAddress());
    Assertions.assertEquals(targetAddress, metadata.getTargetAddress());
    Assertions.assertEquals(MetadataType.ACCOUNT, metadata.getMetadataType());
    Assertions.assertEquals(value, metadata.getValue());

    byte[] serializedState = metadata.serialize();
    String expectedHex =
        "900E96DC85F6B24AC9C8DB5FFC59C35880C0B722C7A416A790FD35818960C7B18B72F49A5598FA9F712A354DB38EB0760A000000000000000000000000000000000B00536F6D6520537472696E67";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MetadataEntryBuilder builder =
        MetadataEntryBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(metadata.serialize()), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(value, StringEncoder.getString(builder.getValue().getData().array()));
  }

  /** Contructor */
  @Test
  public void constructorMosaic() {
    Metadata metadata =
        new Metadata(
            recordId,
            compositeHash,
            sourceAddress,
            targetAddress,
            metadataKey,
            MetadataType.MOSAIC,
            value,
            mosaicId.getIdAsHex());

    Assertions.assertEquals(recordId, metadata.getRecordId().get());
    Assertions.assertEquals(mosaicId, metadata.getTargetId().get());
    Assertions.assertEquals(compositeHash, metadata.getCompositeHash());
    Assertions.assertEquals(sourceAddress, metadata.getSourceAddress());
    Assertions.assertEquals(targetAddress, metadata.getTargetAddress());
    Assertions.assertEquals(MetadataType.MOSAIC, metadata.getMetadataType());
    Assertions.assertEquals(value, metadata.getValue());

    byte[] serializedState = metadata.serialize();
    String expectedHex =
        "900E96DC85F6B24AC9C8DB5FFC59C35880C0B722C7A416A790FD35818960C7B18B72F49A5598FA9F712A354DB38EB0760A000000000000004460BA6E125F9C1C010B00536F6D6520537472696E67";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MetadataEntryBuilder builder =
        MetadataEntryBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(metadata.serialize()), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(value, StringEncoder.getString(builder.getValue().getData().array()));
  }
}
