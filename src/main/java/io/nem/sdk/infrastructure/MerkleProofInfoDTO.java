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
 * MerkleProofInfoDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class MerkleProofInfoDTO {
  @SerializedName("payload")
  private MerkleProofInfoPayload payload = null;

  @SerializedName("type")
  private String type = null;

  public MerkleProofInfoDTO payload(MerkleProofInfoPayload payload) {
    this.payload = payload;
    return this;
  }

   /**
   * Get payload
   * @return payload
  **/
  @ApiModelProperty(required = true, value = "")
  public MerkleProofInfoPayload getPayload() {
    return payload;
  }

  public void setPayload(MerkleProofInfoPayload payload) {
    this.payload = payload;
  }

  public MerkleProofInfoDTO type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(example = "merkleProofInfo", required = true, value = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerkleProofInfoDTO merkleProofInfoDTO = (MerkleProofInfoDTO) o;
    return Objects.equals(this.payload, merkleProofInfoDTO.payload) &&
        Objects.equals(this.type, merkleProofInfoDTO.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerkleProofInfoDTO {\n");

    sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

