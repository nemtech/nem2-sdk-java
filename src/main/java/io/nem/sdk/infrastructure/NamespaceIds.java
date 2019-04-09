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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * NamespaceIds
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class NamespaceIds {
  @SerializedName("namespaceIds")
  private List<String> namespaceIds = null;

  public NamespaceIds namespaceIds(List<String> namespaceIds) {
    this.namespaceIds = namespaceIds;
    return this;
  }

  public NamespaceIds addNamespaceIdsItem(String namespaceIdsItem) {
    if (this.namespaceIds == null) {
      this.namespaceIds = new ArrayList<String>();
    }
    this.namespaceIds.add(namespaceIdsItem);
    return this;
  }

   /**
   * Get namespaceIds
   * @return namespaceIds
  **/
  @ApiModelProperty(example = "[\"84b3552d375ffa4b\"]", value = "")
  public List<String> getNamespaceIds() {
    return namespaceIds;
  }

  public void setNamespaceIds(List<String> namespaceIds) {
    this.namespaceIds = namespaceIds;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamespaceIds namespaceIds = (NamespaceIds) o;
    return Objects.equals(this.namespaceIds, namespaceIds.namespaceIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespaceIds);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NamespaceIds {\n");

    sb.append("    namespaceIds: ").append(toIndentedString(namespaceIds)).append("\n");
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

