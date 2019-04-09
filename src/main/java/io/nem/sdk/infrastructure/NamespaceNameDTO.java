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
 * NamespaceNameDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class NamespaceNameDTO {
  @SerializedName("parentId")
  private UInt64 parentId = null;

  @SerializedName("namespaceId")
  private UInt64 namespaceId = null;

  @SerializedName("name")
  private String name = null;

  public NamespaceNameDTO parentId(UInt64 parentId) {
    this.parentId = parentId;
    return this;
  }

   /**
   * Get parentId
   * @return parentId
  **/
  @ApiModelProperty(value = "")
  public UInt64 getParentId() {
    return parentId;
  }

  public void setParentId(UInt64 parentId) {
    this.parentId = parentId;
  }

  public NamespaceNameDTO namespaceId(UInt64 namespaceId) {
    this.namespaceId = namespaceId;
    return this;
  }

   /**
   * Get namespaceId
   * @return namespaceId
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(UInt64 namespaceId) {
    this.namespaceId = namespaceId;
  }

  public NamespaceNameDTO name(String name) {
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
    NamespaceNameDTO namespaceNameDTO = (NamespaceNameDTO) o;
    return Objects.equals(this.parentId, namespaceNameDTO.parentId) &&
        Objects.equals(this.namespaceId, namespaceNameDTO.namespaceId) &&
        Objects.equals(this.name, namespaceNameDTO.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, namespaceId, name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NamespaceNameDTO {\n");

    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    namespaceId: ").append(toIndentedString(namespaceId)).append("\n");
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

