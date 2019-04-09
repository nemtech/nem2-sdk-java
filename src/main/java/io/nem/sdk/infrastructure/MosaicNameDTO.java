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
 * MosaicNameDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class MosaicNameDTO {
  @SerializedName("parentId")
  private UInt64 parentId = null;

  @SerializedName("mosaicId")
  private UInt64 mosaicId = null;

  @SerializedName("name")
  private String name = null;

  public MosaicNameDTO parentId(UInt64 parentId) {
    this.parentId = parentId;
    return this;
  }

   /**
   * Get parentId
   * @return parentId
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getParentId() {
    return parentId;
  }

  public void setParentId(UInt64 parentId) {
    this.parentId = parentId;
  }

  public MosaicNameDTO mosaicId(UInt64 mosaicId) {
    this.mosaicId = mosaicId;
    return this;
  }

   /**
   * Get mosaicId
   * @return mosaicId
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getMosaicId() {
    return mosaicId;
  }

  public void setMosaicId(UInt64 mosaicId) {
    this.mosaicId = mosaicId;
  }

  public MosaicNameDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MosaicNameDTO mosaicNameDTO = (MosaicNameDTO) o;
    return Objects.equals(this.parentId, mosaicNameDTO.parentId) &&
        Objects.equals(this.mosaicId, mosaicNameDTO.mosaicId) &&
        Objects.equals(this.name, mosaicNameDTO.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, mosaicId, name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MosaicNameDTO {\n");

    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    mosaicId: ").append(toIndentedString(mosaicId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

