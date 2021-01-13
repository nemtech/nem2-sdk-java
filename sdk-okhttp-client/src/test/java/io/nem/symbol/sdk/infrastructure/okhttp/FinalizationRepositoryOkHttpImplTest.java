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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.model.finalization.FinalizationProof;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BmTreeSignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.FinalizationProofDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MessageGroup;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ParentPublicKeySignaturePair;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.StageEnum;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link FinalizationRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class FinalizationRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private FinalizationRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new FinalizationRepositoryOkHttpImpl(apiClientMock);
  }

  @Override
  protected AbstractRepositoryOkHttpImpl getRepository() {
    return repository;
  }

  @Test
  public void getFinalizationProofAtEpoch() throws Exception {
    FinalizationProofDTO dto = createFinalizationProofDTO();
    mockRemoteCall(dto);
    FinalizationProof finalizationProof =
        repository.getFinalizationProofAtEpoch(123).toFuture().get();
    validate(finalizationProof, dto);
  }

  @Test
  public void getFinalizationProofAtHeight() throws Exception {
    FinalizationProofDTO dto = createFinalizationProofDTO();
    mockRemoteCall(dto);
    FinalizationProof finalizationProof =
        repository.getFinalizationProofAtHeight(BigInteger.valueOf(123)).toFuture().get();
    validate(finalizationProof, dto);
  }

  private void validate(FinalizationProof actual, FinalizationProofDTO expected) {
    Assertions.assertEquals(actual.getFinalizationEpoch(), expected.getFinalizationEpoch());
    Assertions.assertEquals(actual.getFinalizationPoint(), expected.getFinalizationPoint());
    Assertions.assertEquals(actual.getHash(), expected.getHash());
    Assertions.assertEquals(actual.getHeight(), expected.getHeight());
    Assertions.assertEquals(actual.getMessageGroups().size(), expected.getMessageGroups().size());
  }

  private FinalizationProofDTO createFinalizationProofDTO() {
    FinalizationProofDTO dto = new FinalizationProofDTO();
    dto.finalizationEpoch(1L);
    dto.finalizationPoint(2L);
    dto.setVersion(3);
    dto.hash("abc");
    dto.height(BigInteger.valueOf(4));

    MessageGroup messageGroup = new MessageGroup();
    messageGroup.stage(StageEnum.NUMBER_1);
    messageGroup.addHashesItem("hash1");
    messageGroup.setHeight(BigInteger.valueOf(20));
    messageGroup.addSignaturesItem(
        new BmTreeSignature()
            .bottom(new ParentPublicKeySignaturePair().signature("sp").parentPublicKey("pp"))
            .root(new ParentPublicKeySignaturePair().signature("sr").parentPublicKey("pr")));
    dto.addMessageGroupsItem(messageGroup);
    return dto;
  }
}
