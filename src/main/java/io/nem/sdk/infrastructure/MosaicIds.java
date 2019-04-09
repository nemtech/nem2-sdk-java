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
 * MosaicIds
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class MosaicIds {
  @SerializedName("mosaicIds")
  private List<String> mosaicIds = null;

  public MosaicIds mosaicIds(List<String> mosaicIds) {
    this.mosaicIds = mosaicIds;
    return this;
  }

  public MosaicIds addMosaicIdsItem(String mosaicIdsItem) {
    if (this.mosaicIds == null) {
      this.mosaicIds = new ArrayList<String>();
    }
    this.mosaicIds.add(mosaicIdsItem);
    return this;
  }

   /**
   * Get mosaicIds
   * @return mosaicIds
  **/
  @ApiModelProperty(example = "[\"d525ad41d95fcf29\"]", value = "")
  public List<String> getMosaicIds() {
    return mosaicIds;
  }

  public void setMosaicIds(List<String> mosaicIds) {
    this.mosaicIds = mosaicIds;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MosaicIds mosaicIds = (MosaicIds) o;
    return Objects.equals(this.mosaicIds, mosaicIds.mosaicIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mosaicIds);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MosaicIds {\n");

    sb.append("    mosaicIds: ").append(toIndentedString(mosaicIds)).append("\n");
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

