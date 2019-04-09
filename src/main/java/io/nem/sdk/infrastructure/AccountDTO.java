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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The account structure describes an account private key, public key, address and allows signing transactions.
 *
 * @since 1.0
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class AccountDTO {
  @SerializedName("address")
  private String address = null;

  @SerializedName("addressHeight")
  private BigInteger addressHeight = null;

  @SerializedName("publicKey")
  private String publicKey = null;

  @SerializedName("publicKeyHeight")
  private BigInteger publicKeyHeight = null;

  @SerializedName("mosaics")
  private List<MosaicDTO> mosaics = new ArrayList<MosaicDTO>();

  @SerializedName("importance")
  private UInt64 importance = null;

  @SerializedName("importanceHeight")
  private UInt64 importanceHeight = null;

  public AccountDTO address(String address) {
    this.address = address;
    return this;
  }

   /**
   * Get address
   * @return address
  **/
  @ApiModelProperty(required = true, value = "")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public AccountDTO addressHeight(BigInteger addressHeight) {
    this.addressHeight = addressHeight;
    return this;
  }

   /**
   * Get addressHeight
   * @return addressHeight
  **/
  @ApiModelProperty(required = true, value = "")
  public BigInteger getAddressHeight() {
    return addressHeight;
  }

  public void setAddressHeight(BigInteger addressHeight) {
    this.addressHeight = addressHeight;
  }

  public AccountDTO publicKey(String publicKey) {
    this.publicKey = publicKey;
    return this;
  }

   /**
   * Get publicKey
   * @return publicKey
  **/
  @ApiModelProperty(required = true, value = "")
  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public AccountDTO publicKeyHeight(BigInteger publicKeyHeight) {
    this.publicKeyHeight = publicKeyHeight;
    return this;
  }

   /**
   * Get publicKeyHeight
   * @return publicKeyHeight
  **/
  @ApiModelProperty(required = true, value = "")
  public BigInteger getPublicKeyHeight() {
    return publicKeyHeight;
  }

  public void setPublicKeyHeight(BigInteger publicKeyHeight) {
    this.publicKeyHeight = publicKeyHeight;
  }

  public AccountDTO mosaics(List<MosaicDTO> mosaics) {
    this.mosaics = mosaics;
    return this;
  }

  public AccountDTO addMosaicsItem(MosaicDTO mosaicsItem) {
    this.mosaics.add(mosaicsItem);
    return this;
  }

   /**
   * Get mosaics
   * @return mosaics
  **/
  @ApiModelProperty(required = true, value = "")
  public List<MosaicDTO> getMosaics() {
    return mosaics;
  }

  public void setMosaics(List<MosaicDTO> mosaics) {
    this.mosaics = mosaics;
  }

  public AccountDTO importance(UInt64 importance) {
    this.importance = importance;
    return this;
  }

   /**
   * Get importance
   * @return importance
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getImportance() {
    return importance;
  }

  public void setImportance(UInt64 importance) {
    this.importance = importance;
  }

  public AccountDTO importanceHeight(UInt64 importanceHeight) {
    this.importanceHeight = importanceHeight;
    return this;
  }

   /**
   * Get importanceHeight
   * @return importanceHeight
  **/
  @ApiModelProperty(required = true, value = "")
  public UInt64 getImportanceHeight() {
    return importanceHeight;
  }

  public void setImportanceHeight(UInt64 importanceHeight) {
    this.importanceHeight = importanceHeight;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountDTO accountDTO = (AccountDTO) o;
    return Objects.equals(this.address, accountDTO.address) &&
        Objects.equals(this.addressHeight, accountDTO.addressHeight) &&
        Objects.equals(this.publicKey, accountDTO.publicKey) &&
        Objects.equals(this.publicKeyHeight, accountDTO.publicKeyHeight) &&
        Objects.equals(this.mosaics, accountDTO.mosaics) &&
        Objects.equals(this.importance, accountDTO.importance) &&
        Objects.equals(this.importanceHeight, accountDTO.importanceHeight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, addressHeight, publicKey, publicKeyHeight, mosaics, importance, importanceHeight);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountDTO {\n");

    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    addressHeight: ").append(toIndentedString(addressHeight)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
    sb.append("    publicKeyHeight: ").append(toIndentedString(publicKeyHeight)).append("\n");
    sb.append("    mosaics: ").append(toIndentedString(mosaics)).append("\n");
    sb.append("    importance: ").append(toIndentedString(importance)).append("\n");
    sb.append("    importanceHeight: ").append(toIndentedString(importanceHeight)).append("\n");
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