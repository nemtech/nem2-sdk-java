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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests of {@link BlockService}. */
class BlockServiceTest {

  private BlockServiceImpl service;
  private BlockRepository blockRepositoryMock;

  @BeforeEach
  void setup() {

    RepositoryFactory factory = Mockito.mock(RepositoryFactory.class);
    blockRepositoryMock = Mockito.mock(BlockRepository.class);
    Mockito.when(factory.createBlockRepository()).thenReturn(blockRepositoryMock);

    service = new BlockServiceImpl(factory);
  }

  @Test
  void isValidTransactionInBlockEmtpyNotEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "ABCD";
    String root = "1234";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleTransaction(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertFalse(service.isValidTransactionInBlock(height, leaf).toFuture().get());
  }

  @Test
  void isValidTransactionInBlockEmptyEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "ABCD";
    String root = "ABCD";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleTransaction(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertTrue(service.isValidTransactionInBlock(height, leaf).toFuture().get());
  }

  @Test
  void isValidTransactionInBlockMultipleEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String hash = "1234";
    String root = "d7de53a6ec87b3cb8e0fb4d6d9aa40b96a17a54b7206702229a6517e91d88dcb";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    merklePath.add(new MerklePathItem(Position.LEFT, "11"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "22"));
    merklePath.add(new MerklePathItem(Position.LEFT, "33"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "44"));
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleTransaction(height, hash))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertTrue(service.isValidTransactionInBlock(height, hash).toFuture().get());
  }

  @Test
  void isValidTransactionInBlockOnError() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String hash = "1234";

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.error(new RuntimeException("Some Error When getting Block")));

    List<MerklePathItem> merklePath = new ArrayList<>();
    merklePath.add(new MerklePathItem(Position.LEFT, "11"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "22"));
    merklePath.add(new MerklePathItem(Position.LEFT, "33"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "44"));
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleTransaction(height, hash))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertFalse(service.isValidTransactionInBlock(height, hash).toFuture().get());
  }

  @Test
  void isValidTransactionInBlockMultipleNotEquals()
      throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "1234";
    String root = "00000";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    merklePath.add(new MerklePathItem(Position.LEFT, "11"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "22"));
    merklePath.add(new MerklePathItem(Position.LEFT, "33"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "44"));
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleTransaction(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertFalse(service.isValidTransactionInBlock(height, leaf).toFuture().get());
  }

  @Test
  void isValidStatementInBlockEmtpyNotEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "ABCD";
    String root = "1234";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleReceipts(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertFalse(service.isValidStatementInBlock(height, leaf).toFuture().get());
  }

  @Test
  void isValidStatementInBlockEmpty() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "ABCD";
    String root = "ABCD";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockReceiptsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleReceipts(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertTrue(service.isValidStatementInBlock(height, leaf).toFuture().get());
  }

  @Test
  void isValidStatementInBlockMultipleEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String hash = "1234";
    String root = "d7de53a6ec87b3cb8e0fb4d6d9aa40b96a17a54b7206702229a6517e91d88dcb";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockReceiptsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    merklePath.add(new MerklePathItem(Position.LEFT, "11"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "22"));
    merklePath.add(new MerklePathItem(Position.LEFT, "33"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "44"));
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleReceipts(height, hash))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertTrue(service.isValidStatementInBlock(height, hash).toFuture().get());
  }

  @Test
  void isValidStatementInBlockOnError() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String hash = "1234";
    String root = "d7de53a6ec87b3cb8e0fb4d6d9aa40b96a17a54b7206702229a6517e91d88dcb";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockReceiptsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));
    Mockito.when(blockRepositoryMock.getMerkleReceipts(height, hash))
        .thenReturn(Observable.error(new RuntimeException("Some Error When getMerkleReceipts")));
    Assertions.assertFalse(service.isValidStatementInBlock(height, hash).toFuture().get());
  }

  @Test
  void isValidStatementInBlockMultipleNotEquals() throws ExecutionException, InterruptedException {

    BigInteger height = BigInteger.ONE;
    String leaf = "1234";
    String root = "00000";

    BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
    Mockito.when(blockInfo.getBlockTransactionsHash()).thenReturn(root);

    Mockito.when(blockRepositoryMock.getBlockByHeight(height))
        .thenReturn(Observable.just(blockInfo));

    List<MerklePathItem> merklePath = new ArrayList<>();
    merklePath.add(new MerklePathItem(Position.LEFT, "11"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "22"));
    merklePath.add(new MerklePathItem(Position.LEFT, "33"));
    merklePath.add(new MerklePathItem(Position.RIGHT, "44"));
    MerkleProofInfo merkleProofInfo = new MerkleProofInfo(merklePath);
    Mockito.when(blockRepositoryMock.getMerkleReceipts(height, leaf))
        .thenReturn(Observable.just(merkleProofInfo));

    Assertions.assertFalse(service.isValidStatementInBlock(height, leaf).toFuture().get());
  }
}
