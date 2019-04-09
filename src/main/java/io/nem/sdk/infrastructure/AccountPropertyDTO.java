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
 * AccountPropertyDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class AccountPropertyDTO {
  @SerializedName("propertyType")
  private Integer propertyType = null;

  @SerializedName("values")
  private List<Object> values = new ArrayList<Object>();

  public AccountPropertyDTO propertyType(Integer propertyType) {
    this.propertyType = propertyType;
    return this;
  }

   /**
   * Get propertyType
   * @return propertyType
  **/
  @ApiModelProperty(example = "1", required = true, value = "")
  public Integer getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(Integer propertyType) {
    this.propertyType = propertyType;
  }

  public AccountPropertyDTO values(List<Object> values) {
    this.values = values;
    return this;
  }

  public AccountPropertyDTO addValuesItem(Object valuesItem) {
    this.values.add(valuesItem);
    return this;
  }

   /**
   * Get values
   * @return values
  **/
  @ApiModelProperty(example = "[\"C14+w2br1wkUdGm/SNSlBwCvcksjWBkBXg==\",\"Ul6vmYP5TAEIOMMcDoHz6sn7bCdpjBaE+Q==\"]", required = true, value = "")
  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountPropertyDTO accountPropertyDTO = (AccountPropertyDTO) o;
    return Objects.equals(this.propertyType, accountPropertyDTO.propertyType) &&
        Objects.equals(this.values, accountPropertyDTO.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyType, values);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountPropertyDTO {\n");

    sb.append("    propertyType: ").append(toIndentedString(propertyType)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

