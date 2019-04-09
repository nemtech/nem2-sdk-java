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
 * NamespaceDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class NamespaceDTO {
  @SerializedName("owner")
  private String owner = null;

  @SerializedName("ownerAddress")
  private String ownerAddress = null;

  @SerializedName("startHeight")
  private UInt64 startHeight = null;

  @SerializedName("endHeight")
  private UInt64 endHeight = null;

  @SerializedName("depth")
  private Integer depth = null;

  @SerializedName("level0")
  private UInt64 level0 = null;

  @SerializedName("level1")
  private UInt64 level1 = null;

  @SerializedName("level2")
  private UInt64 level2 = null;

  @SerializedName("type")
  private Integer type = null;

  @SerializedName("alias")
  private AliasDTO alias = null;

  @SerializedName("parentId")
  private UInt64 parentId = null;

  public NamespaceDTO owner(String owner) {
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

  public NamespaceDTO ownerAddress(String ownerAddress) {
    this.ownerAddress = ownerAddress;
    return this;
  }

   /**
   * Get ownerAddress
   * @return ownerAddress
  **/
  @ApiModelProperty(value = "")
  public String getOwnerAddress() {
    return ownerAddress;
  }

  public void setOwnerAddress(String ownerAddress) {
    this.ownerAddress = ownerAddress;
  }

  public NamespaceDTO startHeight(UInt64 startHeight) {
    this.startHeight = startHeight;
    return this;
  }

   /**
   * Get startHeight
   * @return startHeight
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getStartHeight() {
    return startHeight;
  }

  public void setStartHeight(UInt64 startHeight) {
    this.startHeight = startHeight;
  }

  public NamespaceDTO endHeight(UInt64 endHeight) {
    this.endHeight = endHeight;
    return this;
  }

   /**
   * Get endHeight
   * @return endHeight
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getEndHeight() {
    return endHeight;
  }

  public void setEndHeight(UInt64 endHeight) {
    this.endHeight = endHeight;
  }

  public NamespaceDTO depth(Integer depth) {
    this.depth = depth;
    return this;
  }

   /**
   * Get depth
   * @return depth
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getDepth() {
    return depth;
  }

  public void setDepth(Integer depth) {
    this.depth = depth;
  }

  public NamespaceDTO level0(UInt64 level0) {
    this.level0 = level0;
    return this;
  }

   /**
   * Get level0
   * @return level0
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getLevel0() {
    return level0;
  }

  public void setLevel0(UInt64 level0) {
    this.level0 = level0;
  }

  public NamespaceDTO level1(UInt64 level1) {
    this.level1 = level1;
    return this;
  }

   /**
   * Get level1
   * @return level1
  **/
  @ApiModelProperty(value = "")
  public UInt64 getLevel1() {
    return level1;
  }

  public void setLevel1(UInt64 level1) {
    this.level1 = level1;
  }

  public NamespaceDTO level2(UInt64 level2) {
    this.level2 = level2;
    return this;
  }

   /**
   * Get level2
   * @return level2
  **/
  @ApiModelProperty(value = "")
  public UInt64 getLevel2() {
    return level2;
  }

  public void setLevel2(UInt64 level2) {
    this.level2 = level2;
  }

  public NamespaceDTO type(Integer type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public NamespaceDTO alias(AliasDTO alias) {
    this.alias = alias;
    return this;
  }

   /**
   * Get alias
   * @return alias
  **/
  @ApiModelProperty(required = true, value = "")
  public AliasDTO getAlias() {
    return alias;
  }

  public void setAlias(AliasDTO alias) {
    this.alias = alias;
  }

  public NamespaceDTO parentId(UInt64 parentId) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamespaceDTO namespaceDTO = (NamespaceDTO) o;
    return Objects.equals(this.owner, namespaceDTO.owner) &&
        Objects.equals(this.ownerAddress, namespaceDTO.ownerAddress) &&
        Objects.equals(this.startHeight, namespaceDTO.startHeight) &&
        Objects.equals(this.endHeight, namespaceDTO.endHeight) &&
        Objects.equals(this.depth, namespaceDTO.depth) &&
        Objects.equals(this.level0, namespaceDTO.level0) &&
        Objects.equals(this.level1, namespaceDTO.level1) &&
        Objects.equals(this.level2, namespaceDTO.level2) &&
        Objects.equals(this.type, namespaceDTO.type) &&
        Objects.equals(this.alias, namespaceDTO.alias) &&
        Objects.equals(this.parentId, namespaceDTO.parentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, ownerAddress, startHeight, endHeight, depth, level0, level1, level2, type, alias, parentId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NamespaceDTO {\n");

    sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
    sb.append("    ownerAddress: ").append(toIndentedString(ownerAddress)).append("\n");
    sb.append("    startHeight: ").append(toIndentedString(startHeight)).append("\n");
    sb.append("    endHeight: ").append(toIndentedString(endHeight)).append("\n");
    sb.append("    depth: ").append(toIndentedString(depth)).append("\n");
    sb.append("    level0: ").append(toIndentedString(level0)).append("\n");
    sb.append("    level1: ").append(toIndentedString(level1)).append("\n");
    sb.append("    level2: ").append(toIndentedString(level2)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
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

