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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.AccountPaginationStreamer;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.api.BlockPaginationStreamer;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.MetadataPaginationStreamer;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MosaicPaginationStreamer;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.NamespacePaginationStreamer;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.PaginationStreamer;
import io.nem.symbol.sdk.api.ReceiptPaginationStreamer;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionPaginationStreamer;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.StatePacketType;
import io.nem.symbol.sdk.model.blockchain.StateTree;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockRepositoryIntegrationTest extends BaseIntegrationTest {

  private BlockRepository getBlockRepository(RepositoryType type) {
    return getRepositoryFactory(type).createBlockRepository();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getBlockByHeight(RepositoryType type) {
    BlockInfo blockInfo = get(getBlockRepository(type).getBlockByHeight(BigInteger.valueOf(1)));
    assertEquals(1, blockInfo.getHeight().intValue());
    assertEquals(0, blockInfo.getTimestamp().intValue());
    assertNotEquals(getGenerationHash(), blockInfo.getGenerationHash());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByHeightAsc(RepositoryType type) {
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    criteria.setOrderBy(BlockOrderBy.HEIGHT);
    criteria.setOrder(OrderBy.ASC);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(getBlockRepository(type));

    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    List<BlockInfo> sorted =
        blocks.stream()
            .sorted(Comparator.comparing(BlockInfo::getHeight))
            .collect(Collectors.toList());
    Assertions.assertEquals(blocks, sorted);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchByBeneficiaryAddress(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    BlockInfo block1 = get(blockRepository.getBlockByHeight(BigInteger.ONE));
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    Address expectedBeneficiaryAddress = block1.getBeneficiaryAddress();
    criteria.setBeneficiaryAddress(expectedBeneficiaryAddress);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    blocks.forEach(
        b -> Assertions.assertEquals(expectedBeneficiaryAddress, b.getBeneficiaryAddress()));
    Assertions.assertFalse(blocks.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchByBeneficiaryAddressWhenInvalid(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    Address expectedBeneficiaryAddress = Account.generateNewAccount(getNetworkType()).getAddress();
    criteria.setBeneficiaryAddress(expectedBeneficiaryAddress);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    Assertions.assertTrue(blocks.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchBySignerPublicKey(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    BlockInfo block1 = get(blockRepository.getBlockByHeight(BigInteger.ONE));
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    PublicKey expectedSignerPublicKey = block1.getSignerPublicAccount().getPublicKey();
    criteria.setSignerPublicKey(expectedSignerPublicKey);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    blocks.forEach(
        b ->
            Assertions.assertEquals(
                expectedSignerPublicKey, b.getSignerPublicAccount().getPublicKey()));
    Assertions.assertFalse(blocks.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchBySignerPublicKeyWhenInvalid(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    PublicKey expectedSignerPublicKey = PublicKey.generateRandom();
    criteria.setSignerPublicKey(expectedSignerPublicKey);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    Assertions.assertTrue(blocks.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByHeightDesc(RepositoryType type) {
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    criteria.setOrderBy(BlockOrderBy.HEIGHT);
    criteria.setOrder(OrderBy.DESC);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(getBlockRepository(type));

    List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
    List<BlockInfo> sorted =
        blocks.stream()
            .sorted(Comparator.comparing(BlockInfo::getHeight).reversed())
            .collect(Collectors.toList());
    Assertions.assertEquals(blocks, sorted);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearch(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchBlock(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchBlockPageSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdAsc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdAsc();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void usingBigPageSize(RepositoryType type) {
    getPaginationTester(type).usingBigPageSize();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchUsingOffset(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    criteria.setPageSize(10);
    criteria.setOrderBy(BlockOrderBy.HEIGHT);
    int offsetIndex = 2;
    List<BlockInfo> blocksWithoutOffset = get(streamer.search(criteria).toList().toObservable());
    String offset = blocksWithoutOffset.get(offsetIndex).getHeight().toString();
    criteria.setOffset(offset);
    List<BlockInfo> blockFromOffsets = get(streamer.search(criteria).toList().toObservable());
    List<BlockInfo> expectedList =
        blocksWithoutOffset.stream().skip(offsetIndex + 1).collect(Collectors.toList());
    // If the block grows when running the last search
    PaginationTester.sameEntities(expectedList, blockFromOffsets.subList(0, expectedList.size()));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdDesc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdDesc();
  }

  private PaginationTester<BlockInfo, BlockSearchCriteria> getPaginationTester(
      RepositoryType type) {
    return new PaginationTester<>(BlockSearchCriteria::new, getBlockRepository(type)::search);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void throwExceptionWhenBlockDoesNotExists(RepositoryType type) {
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () -> get(getBlockRepository(type).getBlockByHeight(BigInteger.valueOf(0))));

    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '0'",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleReceiptsFromTransactions(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);

    ReceiptRepository receiptRepository = getRepositoryFactory(type).createReceiptRepository();

    PaginationStreamer<TransactionStatement, TransactionStatementSearchCriteria> streamer =
        ReceiptPaginationStreamer.transactions(receiptRepository);

    List<TransactionStatement> list =
        get(
            streamer
                .search(new TransactionStatementSearchCriteria())
                .take(5)
                .toList()
                .toObservable());

    System.out.println(list.size());

    list.forEach(
        s -> {
          String hash = s.generateHash();
          System.out.println(hash);
          System.out.println(s.getHeight());
          MerkleProofInfo merkleProofInfo =
              get(blockRepository.getMerkleReceipts(s.getHeight(), hash));
          System.out.println(toJson(merkleProofInfo));
        });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleReceiptsFromMosaics(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);

    ReceiptRepository receiptRepository = getRepositoryFactory(type).createReceiptRepository();

    PaginationStreamer<MosaicResolutionStatement, ResolutionStatementSearchCriteria> streamer =
        ReceiptPaginationStreamer.mosaics(receiptRepository);

    List<MosaicResolutionStatement> list =
        get(
            streamer
                .search(new ResolutionStatementSearchCriteria())
                .take(5)
                .toList()
                .toObservable());

    Assertions.assertFalse(list.isEmpty());

    list.forEach(
        s -> {
          String hash = s.generateHash(getNetworkType());
          MerkleProofInfo merkleProofInfo =
              get(blockRepository.getMerkleReceipts(s.getHeight(), hash));
          Assertions.assertFalse(merkleProofInfo.getMerklePath().isEmpty());
        });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleReceiptsFromAddresses(RepositoryType type) {

    Pair<Account, NamespaceId> testAccount = helper().getTestAccount(type);
    helper().basicSendMosaicFromNemesis(type, testAccount.getRight());

    BlockRepository blockRepository = getBlockRepository(type);

    ReceiptRepository receiptRepository = getRepositoryFactory(type).createReceiptRepository();

    PaginationStreamer<AddressResolutionStatement, ResolutionStatementSearchCriteria> streamer =
        ReceiptPaginationStreamer.addresses(receiptRepository);

    List<AddressResolutionStatement> list =
        get(
            streamer
                .search(new ResolutionStatementSearchCriteria())
                .take(5)
                .toList()
                .toObservable());

    Assertions.assertFalse(list.isEmpty());

    list.forEach(
        s -> {
          String hash = s.generateHash(getNetworkType());
          MerkleProofInfo merkleProofInfo =
              get(blockRepository.getMerkleReceipts(s.getHeight(), hash));
          Assertions.assertFalse(merkleProofInfo.getMerklePath().isEmpty());
        });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleTransaction(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);

    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();

    TransactionPaginationStreamer streamer =
        new TransactionPaginationStreamer(transactionRepository);

    List<Transaction> list =
        get(
            streamer
                .search(new TransactionSearchCriteria(TransactionGroup.CONFIRMED))
                .take(5)
                .toList()
                .toObservable());

    Assertions.assertFalse(list.isEmpty());

    list.forEach(
        s -> {
          String hash = s.getTransactionInfo().get().getHash().get();
          BigInteger height = s.getTransactionInfo().get().getHeight();
          MerkleProofInfo merkleProofInfo = get(blockRepository.getMerkleTransaction(height, hash));
          Assertions.assertFalse(merkleProofInfo.getMerklePath().isEmpty());
        });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleStateForAccounts(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    AccountRepository accountRepository = getRepositoryFactory(type).createAccountRepository();

    AccountPaginationStreamer streamer = new AccountPaginationStreamer(accountRepository);

    List<AccountInfo> accountInfos =
        get(streamer.search(new AccountSearchCriteria()).toList().toObservable());

    accountInfos.stream()
        .forEach(
            a -> {
              try {
                byte[] state = a.serialize();
                String hash = ConvertUtils.toHex(Hashes.sha3_256(state));
                StateTree stateTree =
                    get(blockRepository.getMerkleState(StatePacketType.ACCOUNT_STATE_PATH, hash));
                System.out.println(toJson(stateTree));
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleStateForMosaics(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    MosaicRepository mosaicRepository = getRepositoryFactory(type).createMosaicRepository();

    MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(mosaicRepository);

    List<MosaicInfo> accountInfos =
        get(streamer.search(new MosaicSearchCriteria()).toList().toObservable());

    accountInfos.stream()
        .forEach(
            a -> {
              try {
                byte[] state = a.serialize();
                String hash = ConvertUtils.toHex(Hashes.sha3_256(state));
                StateTree stateTree =
                    get(blockRepository.getMerkleState(StatePacketType.MOSAIC_STATE_PATH, hash));
                System.out.println(toJson(stateTree));
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkleStateForMetadata(RepositoryType type) {
    BlockRepository blockRepository = getBlockRepository(type);
    MetadataRepository metadataRepository = getRepositoryFactory(type).createMetadataRepository();

    MetadataPaginationStreamer streamer = new MetadataPaginationStreamer(metadataRepository);

    List<Metadata> accountInfos =
        get(streamer.search(new MetadataSearchCriteria()).toList().toObservable());

    accountInfos.stream()
        .forEach(
            a -> {
              try {
                byte[] state = a.serialize();
                String hash = ConvertUtils.toHex(Hashes.sha3_256(state));
                StateTree stateTree =
                    get(blockRepository.getMerkleState(StatePacketType.METADATA_STATE_PATH, hash));
                System.out.println(toJson(stateTree));
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getNamespaceState(RepositoryType type) {
    NamespaceRepository repository = getRepositoryFactory(type).createNamespaceRepository();
    NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(repository);
    BlockRepository blockRepository = getBlockRepository(type);

    List<NamespaceInfo> infos =
        get(
            streamer
                .search(new NamespaceSearchCriteria())
                .filter(NamespaceInfo::isRoot)
                .toList()
                .toObservable());
    infos.forEach(
        namespaceInfo -> {
          Pair<NamespaceInfo, List<NamespaceInfo>> pair =
              get(getNamespaceState(type, namespaceInfo.getId()));
          byte[] state = pair.getKey().serialize(pair.getRight());
          String hash = ConvertUtils.toHex(Hashes.sha3_256(state));
          try {
            StateTree stateTree =
                get(blockRepository.getMerkleState(StatePacketType.NAMESPACE_STATE_PATH, hash));
            System.out.println(toJson(stateTree));
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
        });
  }

  Observable<Pair<NamespaceInfo, List<NamespaceInfo>>> getNamespaceState(
      RepositoryType type, NamespaceId namespaceId) {

    NamespaceRepository repository = getRepositoryFactory(type).createNamespaceRepository();
    NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(repository);

    return repository
        .getNamespace(namespaceId)
        .flatMap(
            info -> {
              if (!info.isRoot()) {
                return Observable.error(
                    new IllegalArgumentException(
                        "Namespace " + namespaceId.getIdAsHex() + " is not a root namespace"));
              } else {
                return streamer
                    .search(new NamespaceSearchCriteria().level0(namespaceId.getIdAsHex()))
                    .toList()
                    .toObservable()
                    .map(children -> Pair.of(info, children));
              }
            });
  }
}
