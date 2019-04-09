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

import com.google.gson.annotations.SerializedName;
import io.nem.sdk.model.transaction.UInt64;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * BlockDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class BlockDTO {
  @SerializedName("signature")
  private String signature = null;

  @SerializedName("signer")
  private String signer = null;

  @SerializedName("version")
  private UInt64 version = null;

  @SerializedName("type")
  private UInt64 type = null;

  @SerializedName("height")
  private UInt64 height = null;

  @SerializedName("timestamp")
  private UInt64 timestamp = null;

  @SerializedName("difficulty")
  private UInt64 difficulty = null;

  @SerializedName("feeMultiplier")
  private Integer feeMultiplier = null;

  @SerializedName("previousBlockHash")
  private String previousBlockHash = null;

  @SerializedName("blockTransactionsHash")
  private String blockTransactionsHash = null;

  @SerializedName("blockReceiptsHash")
  private String blockReceiptsHash = null;

  @SerializedName("stateHash")
  private String stateHash = null;

  @SerializedName("beneficiaryPublicKey")
  private String beneficiaryPublicKey = null;

  public BlockDTO signature(String signature) {
    this.signature = signature;
    return this;
  }

   /**
   * Get signature
   * @return signature
  **/
  @ApiModelProperty(required = true, value = "")
  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public BlockDTO signer(String signer) {
    this.signer = signer;
    return this;
  }

   /**
   * Get signer
   * @return signer
  **/
  @ApiModelProperty(required = true, value = "")
  public String getSigner() {
    return signer;
  }

  public void setSigner(String signer) {
    this.signer = signer;
  }

  public BlockDTO version(UInt64 version) {
    this.version = version;
    return this;
  }

   /**
   * Get version
   * @return version
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getVersion() {
    return version;
  }

  public void setVersion(UInt64 version) {
    this.version = version;
  }

  public BlockDTO type(UInt64 type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getType() {
    return type;
  }

  public void setType(UInt64 type) {
    this.type = type;
  }

  public BlockDTO height(UInt64 height) {
    this.height = height;
    return this;
  }

   /**
   * Get height
   * @return height
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getHeight() {
    return height;
  }

  public void setHeight(UInt64 height) {
    this.height = height;
  }

  public BlockDTO timestamp(UInt64 timestamp) {
    this.timestamp = timestamp;
    return this;
  }

   /**
   * Get timestamp
   * @return timestamp
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(UInt64 timestamp) {
    this.timestamp = timestamp;
  }

  public BlockDTO difficulty(UInt64 difficulty) {
    this.difficulty = difficulty;
    return this;
  }

   /**
   * Get difficulty
   * @return difficulty
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(UInt64 difficulty) {
    this.difficulty = difficulty;
  }

  public BlockDTO feeMultiplier(Integer feeMultiplier) {
    this.feeMultiplier = feeMultiplier;
    return this;
  }

   /**
   * Get feeMultiplier
   * @return feeMultiplier
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getFeeMultiplier() {
    return feeMultiplier;
  }

  public void setFeeMultiplier(Integer feeMultiplier) {
    this.feeMultiplier = feeMultiplier;
  }

  public BlockDTO previousBlockHash(String previousBlockHash) {
    this.previousBlockHash = previousBlockHash;
    return this;
  }

   /**
   * Get previousBlockHash
   * @return previousBlockHash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getPreviousBlockHash() {
    return previousBlockHash;
  }

  public void setPreviousBlockHash(String previousBlockHash) {
    this.previousBlockHash = previousBlockHash;
  }

  public BlockDTO blockTransactionsHash(String blockTransactionsHash) {
    this.blockTransactionsHash = blockTransactionsHash;
    return this;
  }

   /**
   * Get blockTransactionsHash
   * @return blockTransactionsHash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getBlockTransactionsHash() {
    return blockTransactionsHash;
  }

  public void setBlockTransactionsHash(String blockTransactionsHash) {
    this.blockTransactionsHash = blockTransactionsHash;
  }

  public BlockDTO blockReceiptsHash(String blockReceiptsHash) {
    this.blockReceiptsHash = blockReceiptsHash;
    return this;
  }

   /**
   * Get blockReceiptsHash
   * @return blockReceiptsHash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getBlockReceiptsHash() {
    return blockReceiptsHash;
  }

  public void setBlockReceiptsHash(String blockReceiptsHash) {
    this.blockReceiptsHash = blockReceiptsHash;
  }

  public BlockDTO stateHash(String stateHash) {
    this.stateHash = stateHash;
    return this;
  }

   /**
   * Get stateHash
   * @return stateHash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getStateHash() {
    return stateHash;
  }

  public void setStateHash(String stateHash) {
    this.stateHash = stateHash;
  }

  public BlockDTO beneficiaryPublicKey(String beneficiaryPublicKey) {
    this.beneficiaryPublicKey = beneficiaryPublicKey;
    return this;
  }

   /**
   * Get beneficiaryPublicKey
   * @return beneficiaryPublicKey
  **/
  @ApiModelProperty(required = true, value = "")
  public String getBeneficiaryPublicKey() {
    return beneficiaryPublicKey;
  }

  public void setBeneficiaryPublicKey(String beneficiaryPublicKey) {
    this.beneficiaryPublicKey = beneficiaryPublicKey;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockDTO blockDTO = (BlockDTO) o;
    return Objects.equals(this.signature, blockDTO.signature) &&
        Objects.equals(this.signer, blockDTO.signer) &&
        Objects.equals(this.version, blockDTO.version) &&
        Objects.equals(this.type, blockDTO.type) &&
        Objects.equals(this.height, blockDTO.height) &&
        Objects.equals(this.timestamp, blockDTO.timestamp) &&
        Objects.equals(this.difficulty, blockDTO.difficulty) &&
        Objects.equals(this.feeMultiplier, blockDTO.feeMultiplier) &&
        Objects.equals(this.previousBlockHash, blockDTO.previousBlockHash) &&
        Objects.equals(this.blockTransactionsHash, blockDTO.blockTransactionsHash) &&
        Objects.equals(this.blockReceiptsHash, blockDTO.blockReceiptsHash) &&
        Objects.equals(this.stateHash, blockDTO.stateHash) &&
        Objects.equals(this.beneficiaryPublicKey, blockDTO.beneficiaryPublicKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signature, signer, version, type, height, timestamp, difficulty, feeMultiplier, previousBlockHash, blockTransactionsHash, blockReceiptsHash, stateHash, beneficiaryPublicKey);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockDTO {\n");

    sb.append("    signature: ").append(toIndentedString(signature)).append("\n");
    sb.append("    signer: ").append(toIndentedString(signer)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    difficulty: ").append(toIndentedString(difficulty)).append("\n");
    sb.append("    feeMultiplier: ").append(toIndentedString(feeMultiplier)).append("\n");
    sb.append("    previousBlockHash: ").append(toIndentedString(previousBlockHash)).append("\n");
    sb.append("    blockTransactionsHash: ").append(toIndentedString(blockTransactionsHash)).append("\n");
    sb.append("    blockReceiptsHash: ").append(toIndentedString(blockReceiptsHash)).append("\n");
    sb.append("    stateHash: ").append(toIndentedString(stateHash)).append("\n");
    sb.append("    beneficiaryPublicKey: ").append(toIndentedString(beneficiaryPublicKey)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

