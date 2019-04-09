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
 * BlockMetaDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class BlockMetaDTO {
  @SerializedName("hash")
  private String hash = null;

  @SerializedName("generationHash")
  private String generationHash = null;

  @SerializedName("totalFee")
  private UInt64 totalFee = null;

  @SerializedName("numTransactions")
  private UInt64 numTransactions = null;

  public BlockMetaDTO hash(String hash) {
    this.hash = hash;
    return this;
  }

   /**
   * Get hash
   * @return hash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public BlockMetaDTO generationHash(String generationHash) {
    this.generationHash = generationHash;
    return this;
  }

   /**
   * Get generationHash
   * @return generationHash
  **/
  @ApiModelProperty(required = true, value = "")
  public String getGenerationHash() {
    return generationHash;
  }

  public void setGenerationHash(String generationHash) {
    this.generationHash = generationHash;
  }

  public BlockMetaDTO totalFee(UInt64 totalFee) {
    this.totalFee = totalFee;
    return this;
  }

   /**
   * Get totalFee
   * @return totalFee
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getTotalFee() {
    return totalFee;
  }

  public void setTotalFee(UInt64 totalFee) {
    this.totalFee = totalFee;
  }

  public BlockMetaDTO numTransactions(UInt64 numTransactions) {
    this.numTransactions = numTransactions;
    return this;
  }

   /**
   * Get numTransactions
   * @return numTransactions
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getNumTransactions() {
    return numTransactions;
  }

  public void setNumTransactions(UInt64 numTransactions) {
    this.numTransactions = numTransactions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockMetaDTO blockMetaDTO = (BlockMetaDTO) o;
    return Objects.equals(this.hash, blockMetaDTO.hash) &&
        Objects.equals(this.generationHash, blockMetaDTO.generationHash) &&
        Objects.equals(this.totalFee, blockMetaDTO.totalFee) &&
        Objects.equals(this.numTransactions, blockMetaDTO.numTransactions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hash, generationHash, totalFee, numTransactions);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockMetaDTO {\n");

    sb.append("    hash: ").append(toIndentedString(hash)).append("\n");
    sb.append("    generationHash: ").append(toIndentedString(generationHash)).append("\n");
    sb.append("    totalFee: ").append(toIndentedString(totalFee)).append("\n");
    sb.append("    numTransactions: ").append(toIndentedString(numTransactions)).append("\n");
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

