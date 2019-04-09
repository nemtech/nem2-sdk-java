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
 * AccountPropertiesInfoDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class AccountPropertiesInfoDTO {
  @SerializedName("meta")
  private AccountPropertiesMetaDTO meta = null;

  @SerializedName("accountProperties")
  private AccountPropertiesDTO accountProperties = null;

  public AccountPropertiesInfoDTO meta(AccountPropertiesMetaDTO meta) {
    this.meta = meta;
    return this;
  }

   /**
   * Get meta
   * @return meta
  **/
  @ApiModelProperty(required = true, value = "")
  public AccountPropertiesMetaDTO getMeta() {
    return meta;
  }

  public void setMeta(AccountPropertiesMetaDTO meta) {
    this.meta = meta;
  }

  public AccountPropertiesInfoDTO accountProperties(AccountPropertiesDTO accountProperties) {
    this.accountProperties = accountProperties;
    return this;
  }

   /**
   * Get accountProperties
   * @return accountProperties
  **/
  @ApiModelProperty(required = true, value = "")
  public AccountPropertiesDTO getAccountProperties() {
    return accountProperties;
  }

  public void setAccountProperties(AccountPropertiesDTO accountProperties) {
    this.accountProperties = accountProperties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountPropertiesInfoDTO accountPropertiesInfoDTO = (AccountPropertiesInfoDTO) o;
    return Objects.equals(this.meta, accountPropertiesInfoDTO.meta) &&
        Objects.equals(this.accountProperties, accountPropertiesInfoDTO.accountProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(meta, accountProperties);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountPropertiesInfoDTO {\n");

    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
    sb.append("    accountProperties: ").append(toIndentedString(accountProperties)).append("\n");
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

