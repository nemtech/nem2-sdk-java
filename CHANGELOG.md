# CHANGELOG
All notable changes to this project will be documented in this file.

The changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.17.0] - 24-Feb-2020

**Milestone**: Fushicho.4(RC3 0.9.3.1)

 Versions  |   |
---|---|---
SDK OkHttp| v0.17.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-okhttp-client
SDK Vertx| v0.17.0 | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client
Catbuffer Library| v2.0.2 | https://repo.maven.apache.org/maven2/io/nem/catbuffer-java
Client OkHttp | v0.8.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-openapi-okhttp-gson-client
Client Vertx | v0.8.5  | https://repo.maven.apache.org/maven2/io/nem/symbol-sdk-vertx-client

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


