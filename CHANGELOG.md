# CHANGELOG
All notable changes to this project will be documented in this file.

The changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).


## [0.22.1] - 10-Dec-2020

**Milestone**: Catapult-server finality (0.10.0.4)
 Package  | Version  | Link
---|---|---
SDK OkHttp | v0.22.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx | v0.22.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library | v0.0.24 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.10.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.10.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- Fixed finalization proof schema version compatibility issue.
- Account voting key version compatibility issue.

## [0.22.0] - 8-Dec-2020

**Milestone**: Catapult-server finality (0.10.0.4)
 Package  | Version  | Link
---|---|---
SDK OkHttp | v0.22.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx | v0.22.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library | v0.0.24 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.10.4  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.10.4  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** `Deadline.create` requires the configurable `epochAdjustment` from the network properties. The value can be retrieved using ``RepositoryFactory.getEpochAdjustment()``.
- **[BREAKING CHANGE]** `SecreatLockRepository.getSecretLock` has been removed. You can now search by secret by using the search criteria.
- Added `FinalizationRepository`.
- Added `transferMosaicId`, `fromTransferAmount`, `toTransferAmount` to transaction searches.
- Added `CurrencyService` to allow loading Network and custom `Currency` objects from the rest service.
- Added `StateProofService` to verify the different states.
- Added `serialize()` to state objects `AccountInfo`, `MosaicInfo`, `NamespaceInfo`, `MultisigAccountInfo`, `AccountRestrictions`, `MosaicGlobalRestriction`, `MosaicAddressRestriction`, `MetadataEntry`, `SecretLockInfo`, `HashLockInfo` to generate the state proof hashes.
- Added `version` field to state objects.
- Added `/merkle` endpoints to the repositories of the different states.
- Added `stremer()` to repositories to simplify `PaginationStreamer` objects creation.
- Improved `search` endpoints allowing "empty" criteria in order to paginate over all the objects.
- `Listener` now accepts address aliases as `UnresolvedAddress` objects.
- Added V1 and V2 Voting Key transaction support.
- Updated `FinalizationProof` object added ``SignatureSchema`` for server tree testnet/v3.

## [0.21.0] - 25-Sep-2020

**Milestone**: Catapult-server finality(0.10.0.3)
 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.21.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.21.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.23 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.10.0  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.10.0  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** Updated `ChainRepository` merging Height and Score into Info object. Added finalized block information.
- **[BREAKING CHANGE]** Updated `RestrictionMosaicRepository` adding pagination.
- **[BREAKING CHANGE]** Updated `RestrictionAccountRepository` getter.
- **[BREAKING CHANGE]** Simplified `MosaicNonce`. It works with an `int` instead of a `byte[]`.
- **[BREAKING CHANGE]** Messages in Transfer Transactions are optional. `TransferTransactionFactory` creates a transaction with no message by default. `PlainMessage.Empty` has been removed.
- **[BREAKING CHANGE]** Renamed `numTransactions` and `numStatements` in `BlockInfo` to `transactionsCount` and `statementsCount`.
- Added support for topic/data payload wrapper in WS Listener allowing users to reuse the connection for different channels.
- Added `finalizedBlock` WS Listener subscription
- Added `SecretLockRepository` and `HashLockRepository`
- Improved API around cosignatures and cosigners and how they can be added to aggregate transactions. 
- Added [symbol-bootstrap](https://github.com/nemtech/symbol-bootstrap) integration.
- Added From and To Height filters to `TransactionRepository` searches.
- Restyled code using [spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle).

## [0.20.3] - 14-Aug-2020

**Milestone**: Gorilla.1(0.9.6.4)
 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.20.3 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.20.3 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.22 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.9.6  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.9.6  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** Refactored `Namespace`, `Receipt`, `Account` and `Metadata` endpoints. Added new search endpoints and removed old endpoints.
- **[BREAKING CHANGE]** Updated encryption / decryption algorithm from `AES-CBC` to `AES-GCM` to meet the security standard.
- **[BREAKING CHANGE]** Updated PersistentDelegatedHarvesting message marker. Added VRF private key parameter in PersistentDelegatedHarvesting message & transaction creation.
- Improved negative validation on unsigned numbers.
- Allowing multiple recipient type on transaction statement searches.

## [0.20.2] - 1-Jul-2020

**Milestone**: Gorilla.1(0.9.6.2)
 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.20.2 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.20.2 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.22 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.9.4  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.9.4  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- Added `maxVotingKeysPerAccount`, `minVotingKeyLifetime` and `maxVotingKeyLifetime` in **ChainProperties**.
- Updated rest clients to `0.9.4`.

## [0.20.1] - 29-Jun-2020

**Milestone**: Gorilla.1(0.9.6.2)

 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.20.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.20.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.22 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.9.3  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.9.3  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** Added `startPoint` and `endPoint` in `VotingKeyLinkTransaction`.
- **[BREAKING CHANGE]** Renamed `SupplementalAccountKeys` to `SupplementalPublicKeys`. The new `SupplementalPublicKeys` has been changed from `List` type to an `object` containing: `linked`, `node`, `vrf` and `voting` key(s).
- **[BREAKING CHANGE]** `AccountRestrictionFlags` enum has been split into 3 different enum (`AccountAddressRestrictionFlags`, `AccountMosaicRestrictionFlags` and `AccountOperationRestrictionFlags`).
- **[BREAKING CHANGE]**  Incorrect `int` types have been updated to `long` when catbuffer attribute type is `uint32` (e.g. `feeMultiplier`).

## [0.20.0] - 18-Jun-2020    

**Milestone**: Gorilla.1(0.9.6.1)

 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.20.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.20.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.20 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.9.2  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.9.2  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** Model property name changes:
    1. **MetadataEntry**: senderPublicKey: string => sourceAddress: Address; targetPublicKey: string => targetAddress: Address
    2. **MultisigAccountGraphInfo**: multisigAccounts => multisigEntries
    3. **MultisigAccountInfo**: account: PublicAccount => accountAddress: Address; cosignatories: PublicAccount[] => cosignatoryAddresses: Address; multisigAccounts: PublicAccount[] => multisigAddresses: Address[]
    4. **BlockInfo**: beneficiaryPublicKey: PublicAccount | undefined => beneficiaryAddress: Address | undefined
    5. **MosaicId**: owner: PublicAccount => ownerAddress: Address
    6. **MosaicInfo**: owner: PublicAccount => ownerAddress: Address; height => startHeight.
    7. **NamespaceInfo**: owner: PublicAccount => ownerAddress: Address
    8. **ChainProperties**: harvestNetworkFeeSinkPublicKey => harvestNetworkFeeSinkAddress
    9. **MosaicNetworkProperties**: mosaicRentalFeeSinkPublicKey => mosaicRentalFeeSinkAddress
    10. **NamespaceNetworkProperties**: namespaceRentalFeeSinkPublicKey => namespaceRentalFeeSinkAddress
    11. **NetworkProperties**: publicKey => nemesisSignerPublicKey
    12. **BalanceChangeReceipt**: targetPublicAccount: PublicAccount => targetAddress: Address
    13. **BalanceTransferReceipt**: sender: PublicAccount => senderAddress: Address
- **[BREAKING CHANGE]** Transaction property name changes:
    1. **AccountMetadataTransaction**: targetPublicKey: string => targetAddress: UnresolvedAddress
    2. **MosaicMetadataTransaction**: targetPublicKey: string => targetAddress: UnresolvedAddress
    3. **NamespaceMetadataTransaction**: targetPublicKey: string => targetAddress: UnresolvedAddress
    4. **MultisigAccountModificationTransaction**: publicKeyAdditions: PublicAccount[] => addressAdditions: UnresolvedAddress[]; publicKeyDeletions: PublicAccount[] => addressDeletions: UnresolvedAddress[]
    5. **AggregateTransactionService**: cosignatories: string[] => cosignatories: Address[]
- **[BREAKING CHANGE]** **Address** format changed from 25 bytes to 24 bytes. See new address test vector [here](https://github.com/nemtech/test-vectors/blob/main/1.test-address.json).
- **[BREAKING CHANGE]** MosaicId creation (from Nonce) changed from using **PublicKey** to **Address**. See new mosaicId test vector [here](https://github.com/nemtech/test-vectors/blob/main/5.test-mosaic-id.json).
- **[BREAKING CHANGE]** `BigInteger` **version** field in `CosignatureSignedTransaction` and `AggregateTransactionCosignature` with default value `0` when they are created for the first time.
- **[BREAKING CHANGE]** Removed all transaction get endpoints from **AccountRepository** and **BlockRepository**.
- **[BREAKING CHANGE]** Added `TransactionGroup (required)` parameter in `getTransaction` endpoint in `TransactionRepository`.
- Added `Search` endpoints to `TransactionRepository`, `BlockRepository`, and `MosaicRepository`.

    **Note:**

    1. Search endpoints returns pagination payload (`Page<t>`) rather than raw list.
    2. For **AggregateTransaction**, transaction search endpoint only returns the aggregate wrapper transaction **WITHOUT** embedded transactions. `complete` aggregate payload can be get from `getTransaction` or `getTransactionByIds` endpoints.
- Added SearchCriteria interfaces for the new search endpoints.
- **group** filter in `TransactionSearchCriteria` to be mandatory due to rest endpoint changes.
- Added **PaginationStreamer** for the 3 new search endpoints (`BlockPaginationStreamer`, `MosaicPaginationStreamer`, `TransactionPaginationStreamer`) to improve pagination querying.
- Added `size` in `BlockInfo` model.

## [0.19.0] - 18-May-2020    

**Milestone**: Gorilla.1(0.9.5.1)

 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.19.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.19.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v0.0.15 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.8.10  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.8.10  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** `Transaction signing` is now using `GenerationHashSeed` from `NodeInfo` or `NetworkProperties`. GenerationHash on Nemesis block (block:1) is `NOT` used for signing purposes. `RepositoryFactory.getGenerationHash()` has been updated.
- **[BREAKING CHANGE]** Renamed `AccountLinkTransaction` to `AccountKeyLinkTransaction`.
- **[BREAKING CHANGE]** Renamed `networkGenerationHash` to `networkGenerationHashSeed` in `NodeInfo`.
- **[BREAKING CHANGE]** replaced `linkedPublickKey` with `supplementalAccountKeys` array in `AccountInfo`.
- Added new transaction `VrfKeyLinkTransaction`.
- Added new transaction `VotingKeyLinkTransaction`.
- Added new transaction `NodeKeyLinkTransaction`.
- Added new properties `proofGamma`, `proofScalar`, `proofVarificationHash` in `BlockInfo`
- Added new properties `harvestNetworkPercentage`, `harvestNetworkFeeSinkAddress` in `NetworkProperties`.
- Added new `KeyType`: Unset / Linked / VRF / Voting / Node / All.
- Unified implementation of `PrivateKey` and `PublicKey`. Added `VotingKey`.
- Transaction Factories can now calculate the max fees based on fee multipliers and cosignature configuration.
- Fixed `ConvertUtils.reverseHexString`.
- Added `BinarySerialization.deserializeToFactory`.

## [0.17.1] - 8-Apr-2020    

**Milestone**: Fushicho.4(RC3 0.9.3.2)

 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.17.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.17.1 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v2.0.2 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.8.9  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.8.9  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- Invalid private key generation when hex has leading zeros.
- Removed confusion `isAlias()` from `MosaicId` and `NamespaceId`.
- Added `GetNetworkProperties` to `NetworkRepository`.
- Fixed Hash 160 secret deserialization.

## [0.17.0] - 9-Mar-2020    

**Milestone**: Fushicho.4(RC3 0.9.3.1)

 Package  | Version  | Link
---|---|---
SDK OkHttp| v0.17.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.17.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v2.0.2 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.8.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.8.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-vertx-client/

- **[BREAKING CHANGE]** Changed hashing algorithm to cope with catapult-server changes. All Key derivation and signing are now using `SHA512`. Removed `SignSchema` so `NetworkType` is no longer bonded to the schema anymore (sha3 / keccak). This change will affect all existing keypairs / address (derived from public key) and transaction signatures.
- **[BREAKING CHANGE]** `NetworkCurrencyMosaic` and `NetworkHarvestMosaic` subclasses have been replaced with `NetworkCurrency.CAT_CURRENCY` and `NetworkCurrency.CAT_HARVEST` static values. `NetworkCurrency.SYMBOL_XYM` has been added describing the new currency.
- **[BREAKING CHANGE]** Symbol rebranding. Maven artifact names have been changed. Packages have been moved from `io.nem` to `io.nem.symbol`
- **[BREAKING CHANGE]** Added `s-part` of transaction signature to transaction hash.
- Added `numStatements` to `blockInfo` model.
- Added `NetworkGenerationHash` to the payload in `node/info` endpoint.
- Added enum for block merkle path item positions (`left / right`) to replace previous number type value (`1 / 2`).
- Added new `BlockService` for `Transaction` and `Receipt` block merkle proof auditing.
- Added new node type `Dual` to the existing `RoleType`.
- Added new endpoint `node/health` in `NodeRespository`.
- Moved `getStorageInfo` and `getServerInfo` from `DiagnosticRespository` to `NodeRespository`. `NodeRespository` has been removed.
- Generated hashes are uppercase.
- Fixed ResolutionStatement hash generation.
- Fixed Jackson version compatibility issue.
- Fixed Cosignature listener channel and model object.
- Added Jackson 2 and GSon adapters for JDK 8 compatibility.
- Added `getNodePeers` method to `NodeRepository`
- General legacy code refactoring and cleanup.

## [0.16.2] - 30-Jan-2020

**Milestone**: Fuschicho 3

- Core 0.9.2.1 compatible. Changed hash algorithm for shared key derivation to `HKDF-HMAC-Sha256`.
- Removed `senderPrivateKey` in `Persistent Delegation Request Transaction`. Instead, it uses an `ephemeral key pair` and the `EphemeralPublicKey` is now attached in the `PersistentDelegationMessage` payload.
- Removed `salt` encryption and decryption functions which uses `HKDF-HMAC-Sha256` instead. This only affects the encrypted payload.
- Updated `TransactionType` enum values to match `catabuffer` schema definition.
- Improved Vertx exception handling.

## [0.16.1] - 16-Jan-2020

**Milestone**: Fuschicho 3

- Fixed transaction status code issue in both Http and Websocket due to schema update.
- Added transaction criteria to account repository with the ability of filter by transaction type.
- Added partial transaction queries to the account repository. 
- Fixed empty message mapping in transfer transactions.
- Implemented MosaicRestrictionTransactionService.
- Added missing 'partial' value in TransactionState.
- Other small fixes.

## [0.16.0] - 11-Dec-2019

**Milestone**: Fuschicho 3

- Added TransactionService. A service with many utility methods that simplifies how transactions are announced, validated and resolved.
- Added AggregateTransactionService. A service that allows clients to validate if an Aggregate Transactions is completed with all the necessary signatures.
- Fixed BalanceTransferReceipt serialization
- Added epochAdjustment introduced in catapult-server v0.9.1.1 (Fushicho.3)

## [0.15.0] - 25-Nov-2019

**Milestone**: Fuschicho 2

- Applied latest Fushicho2 schema changes to both transaction serialization and http client codes.
- Added MetadataTransactionService that simplifies how metadata can be set and updated.
- Added 'Mosaic Owned' endpoints.
- Other small fixes.

## [0.14.2] - 04-Nov-2019

**Milestone**: Fuschicho

- Added Address isValidAddress methods.
- Added Transaction size methods to calculate transaction fees.
- Listener status filtered by topic/address.
- NetworkRepository uses Network Routes.
- NetworkType is now present in Address Alias / Unresolved Address Catbuffer serialization.
- Other small fixes.

## [0.14.1] - 24-Oct-2019

**Milestone**: Fuschicho

- Improved Transaction serialization. Signature and signer are serialized when present.
- Upgraded Gradle to latest 5.6.3. Gradle build has been simplified.
- Removed JUnit 4 support. All the tests are now JUnit 5.
- Fixed hardcoded network type in unresolved address serialization.
- Improved release process.
- Other small fixes.

## [0.14.0] - 16-Oct-2019

**Milestone**: Fuschicho

- Applied latest OpenAPI doc (`v0.7.19`).
- Catbuffer binary Serialization and Deserialization for offline work.
- JSON Serialization and Deserialization for offline work.
- Created repository factories.
- Created new rest client repositories.
- Mosaic and address restrictions support
- Address, mosaic and namespace metadata support.
- Mosaic and address aliases support.
- Audit merkle proofs support.
- Improved integration tests.
- Replaced `UInt64DTO` for `BigInteger`.
- Open API generated libraries.
- Vertx and OkHttp rest client support.
- Improved Listeners.
- Other small fixes.


