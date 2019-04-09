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
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * BlockchainStorageInfoDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class BlockchainStorageInfoDTO {
  @SerializedName("numBlocks")
  private Integer numBlocks = null;

  @SerializedName("numTransactions")
  private Integer numTransactions = null;

  @SerializedName("numAccounts")
  private Integer numAccounts = null;

  public BlockchainStorageInfoDTO numBlocks(Integer numBlocks) {
    this.numBlocks = numBlocks;
    return this;
  }

   /**
   * Get numBlocks
   * @return numBlocks
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getNumBlocks() {
    return numBlocks;
  }

  public void setNumBlocks(Integer numBlocks) {
    this.numBlocks = numBlocks;
  }

  public BlockchainStorageInfoDTO numTransactions(Integer numTransactions) {
    this.numTransactions = numTransactions;
    return this;
  }

   /**
   * Get numTransactions
   * @return numTransactions
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getNumTransactions() {
    return numTransactions;
  }

  public void setNumTransactions(Integer numTransactions) {
    this.numTransactions = numTransactions;
  }

  public BlockchainStorageInfoDTO numAccounts(Integer numAccounts) {
    this.numAccounts = numAccounts;
    return this;
  }

   /**
   * Get numAccounts
   * @return numAccounts
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getNumAccounts() {
    return numAccounts;
  }

  public void setNumAccounts(Integer numAccounts) {
    this.numAccounts = numAccounts;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockchainStorageInfoDTO blockchainStorageInfoDTO = (BlockchainStorageInfoDTO) o;
    return Objects.equals(this.numBlocks, blockchainStorageInfoDTO.numBlocks) &&
        Objects.equals(this.numTransactions, blockchainStorageInfoDTO.numTransactions) &&
        Objects.equals(this.numAccounts, blockchainStorageInfoDTO.numAccounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(numBlocks, numTransactions, numAccounts);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockchainStorageInfoDTO {\n");

    sb.append("    numBlocks: ").append(toIndentedString(numBlocks)).append("\n");
    sb.append("    numTransactions: ").append(toIndentedString(numTransactions)).append("\n");
    sb.append("    numAccounts: ").append(toIndentedString(numAccounts)).append("\n");
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

