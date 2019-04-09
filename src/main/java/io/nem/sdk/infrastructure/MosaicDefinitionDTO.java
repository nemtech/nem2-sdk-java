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
 * MosaicDefinitionDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class MosaicDefinitionDTO {
  @SerializedName("mosaicId")
  private UInt64 mosaicId = null;

  @SerializedName("supply")
  private UInt64 supply = null;

  @SerializedName("height")
  private UInt64 height = null;

  @SerializedName("owner")
  private String owner = null;

  @SerializedName("properties")
  private MosaicPropertiesDTO properties = null;

  public MosaicDefinitionDTO mosaicId(UInt64 mosaicId) {
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

  public MosaicDefinitionDTO supply(UInt64 supply) {
    this.supply = supply;
    return this;
  }

   /**
   * Get supply
   * @return supply
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getSupply() {
    return supply;
  }

  public void setSupply(UInt64 supply) {
    this.supply = supply;
  }

  public MosaicDefinitionDTO height(UInt64 height) {
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

  public MosaicDefinitionDTO owner(String owner) {
    this.owner = owner;
    return this;
  }

   /**
   * Get owner
   * @return owner
  **/
  @ApiModelProperty(required = true, value = "")
  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public MosaicDefinitionDTO properties(MosaicPropertiesDTO properties) {
    this.properties = properties;
    return this;
  }

   /**
   * Get properties
   * @return properties
  **/
  @ApiModelProperty(required = true, value = "")
  public MosaicPropertiesDTO getProperties() {
    return properties;
  }

  public void setProperties(MosaicPropertiesDTO properties) {
    this.properties = properties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MosaicDefinitionDTO mosaicDefinitionDTO = (MosaicDefinitionDTO) o;
    return Objects.equals(this.mosaicId, mosaicDefinitionDTO.mosaicId) &&
        Objects.equals(this.supply, mosaicDefinitionDTO.supply) &&
        Objects.equals(this.height, mosaicDefinitionDTO.height) &&
        Objects.equals(this.owner, mosaicDefinitionDTO.owner) &&
        Objects.equals(this.properties, mosaicDefinitionDTO.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mosaicId, supply, height, owner, properties);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MosaicDefinitionDTO {\n");

    sb.append("    mosaicId: ").append(toIndentedString(mosaicId)).append("\n");
    sb.append("    supply: ").append(toIndentedString(supply)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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

