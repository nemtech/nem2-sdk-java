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
 * BlockchainScoreDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class BlockchainScoreDTO {
  @SerializedName("scoreHigh")
  private UInt64 scoreHigh = null;

  @SerializedName("scoreLow")
  private UInt64 scoreLow = null;

  public BlockchainScoreDTO scoreHigh(UInt64 scoreHigh) {
    this.scoreHigh = scoreHigh;
    return this;
  }

   /**
   * Get scoreHigh
   * @return scoreHigh
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getScoreHigh() {
    return scoreHigh;
  }

  public void setScoreHigh(UInt64 scoreHigh) {
    this.scoreHigh = scoreHigh;
  }

  public BlockchainScoreDTO scoreLow(UInt64 scoreLow) {
    this.scoreLow = scoreLow;
    return this;
  }

   /**
   * Get scoreLow
   * @return scoreLow
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getScoreLow() {
    return scoreLow;
  }

  public void setScoreLow(UInt64 scoreLow) {
    this.scoreLow = scoreLow;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockchainScoreDTO blockchainScoreDTO = (BlockchainScoreDTO) o;
    return Objects.equals(this.scoreHigh, blockchainScoreDTO.scoreHigh) &&
        Objects.equals(this.scoreLow, blockchainScoreDTO.scoreLow);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scoreHigh, scoreLow);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockchainScoreDTO {\n");

    sb.append("    scoreHigh: ").append(toIndentedString(scoreHigh)).append("\n");
    sb.append("    scoreLow: ").append(toIndentedString(scoreLow)).append("\n");
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

