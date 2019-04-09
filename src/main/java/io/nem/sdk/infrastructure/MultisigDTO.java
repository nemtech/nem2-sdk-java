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
 * MultisigDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class MultisigDTO {
  @SerializedName("account")
  private String account = null;

  @SerializedName("accountAddress")
  private String accountAddress = null;

  @SerializedName("minApproval")
  private Integer minApproval = null;

  @SerializedName("minRemoval")
  private Integer minRemoval = null;

  @SerializedName("cosignatories")
  private List<String> cosignatories = new ArrayList<String>();

  @SerializedName("multisigAccounts")
  private List<String> multisigAccounts = new ArrayList<String>();

  public MultisigDTO account(String account) {
    this.account = account;
    return this;
  }

   /**
   * Get account
   * @return account
  **/
  @ApiModelProperty(required = true, value = "")
  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public MultisigDTO accountAddress(String accountAddress) {
    this.accountAddress = accountAddress;
    return this;
  }

   /**
   * Get accountAddress
   * @return accountAddress
  **/
  @ApiModelProperty(value = "")
  public String getAccountAddress() {
    return accountAddress;
  }

  public void setAccountAddress(String accountAddress) {
    this.accountAddress = accountAddress;
  }

  public MultisigDTO minApproval(Integer minApproval) {
    this.minApproval = minApproval;
    return this;
  }

   /**
   * Get minApproval
   * @return minApproval
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getMinApproval() {
    return minApproval;
  }

  public void setMinApproval(Integer minApproval) {
    this.minApproval = minApproval;
  }

  public MultisigDTO minRemoval(Integer minRemoval) {
    this.minRemoval = minRemoval;
    return this;
  }

   /**
   * Get minRemoval
   * @return minRemoval
  **/
  @ApiModelProperty(required = true, value = "")
  public Integer getMinRemoval() {
    return minRemoval;
  }

  public void setMinRemoval(Integer minRemoval) {
    this.minRemoval = minRemoval;
  }

  public MultisigDTO cosignatories(List<String> cosignatories) {
    this.cosignatories = cosignatories;
    return this;
  }

  public MultisigDTO addCosignatoriesItem(String cosignatoriesItem) {
    this.cosignatories.add(cosignatoriesItem);
    return this;
  }

   /**
   * Get cosignatories
   * @return cosignatories
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getCosignatories() {
    return cosignatories;
  }

  public void setCosignatories(List<String> cosignatories) {
    this.cosignatories = cosignatories;
  }

  public MultisigDTO multisigAccounts(List<String> multisigAccounts) {
    this.multisigAccounts = multisigAccounts;
    return this;
  }

  public MultisigDTO addMultisigAccountsItem(String multisigAccountsItem) {
    this.multisigAccounts.add(multisigAccountsItem);
    return this;
  }

   /**
   * Get multisigAccounts
   * @return multisigAccounts
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getMultisigAccounts() {
    return multisigAccounts;
  }

  public void setMultisigAccounts(List<String> multisigAccounts) {
    this.multisigAccounts = multisigAccounts;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultisigDTO multisigDTO = (MultisigDTO) o;
    return Objects.equals(this.account, multisigDTO.account) &&
        Objects.equals(this.accountAddress, multisigDTO.accountAddress) &&
        Objects.equals(this.minApproval, multisigDTO.minApproval) &&
        Objects.equals(this.minRemoval, multisigDTO.minRemoval) &&
        Objects.equals(this.cosignatories, multisigDTO.cosignatories) &&
        Objects.equals(this.multisigAccounts, multisigDTO.multisigAccounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(account, accountAddress, minApproval, minRemoval, cosignatories, multisigAccounts);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultisigDTO {\n");

    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    accountAddress: ").append(toIndentedString(accountAddress)).append("\n");
    sb.append("    minApproval: ").append(toIndentedString(minApproval)).append("\n");
    sb.append("    minRemoval: ").append(toIndentedString(minRemoval)).append("\n");
    sb.append("    cosignatories: ").append(toIndentedString(cosignatories)).append("\n");
    sb.append("    multisigAccounts: ").append(toIndentedString(multisigAccounts)).append("\n");
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