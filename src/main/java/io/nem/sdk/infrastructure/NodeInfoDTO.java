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
 * NodeInfoDTO
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class NodeInfoDTO {
  @SerializedName("publicKey")
  private String publicKey = null;

  @SerializedName("port")
  private Integer port = null;

  @SerializedName("networkIdentifier")
  private Integer networkIdentifier = null;

  @SerializedName("version")
  private Integer version = null;

  @SerializedName("roles")
  private Integer roles = null;

  @SerializedName("host")
  private String host = null;

  @SerializedName("friendlyName")
  private String friendlyName = null;

  public NodeInfoDTO publicKey(String publicKey) {
    this.publicKey = publicKey;
    return this;
  }

   /**
   * Get publicKey
   * @return publicKey
  **/
  @ApiModelProperty(example = "EB6839C7E6BD0031FDD5F8CB5137E9BC950D7EE7756C46B767E68F3F58C24390", required = true, value = "")
  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public NodeInfoDTO port(Integer port) {
    this.port = port;
    return this;
  }

   /**
   * Get port
   * @return port
  **/
  @ApiModelProperty(example = "7900", required = true, value = "")
  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public NodeInfoDTO networkIdentifier(Integer networkIdentifier) {
    this.networkIdentifier = networkIdentifier;
    return this;
  }

   /**
   * Get networkIdentifier
   * @return networkIdentifier
  **/
  @ApiModelProperty(example = "144", required = true, value = "")
  public Integer getNetworkIdentifier() {
    return networkIdentifier;
  }

  public void setNetworkIdentifier(Integer networkIdentifier) {
    this.networkIdentifier = networkIdentifier;
  }

  public NodeInfoDTO version(Integer version) {
    this.version = version;
    return this;
  }

   /**
   * Get version
   * @return version
  **/
  @ApiModelProperty(example = "0", required = true, value = "")
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public NodeInfoDTO roles(Integer roles) {
    this.roles = roles;
    return this;
  }

   /**
   * Get roles
   * @return roles
  **/
  @ApiModelProperty(example = "2", required = true, value = "")
  public Integer getRoles() {
    return roles;
  }

  public void setRoles(Integer roles) {
    this.roles = roles;
  }

  public NodeInfoDTO host(String host) {
    this.host = host;
    return this;
  }

   /**
   * Get host
   * @return host
  **/
  @ApiModelProperty(example = "api-node-0", required = true, value = "")
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public NodeInfoDTO friendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
    return this;
  }

   /**
   * Get friendlyName
   * @return friendlyName
  **/
  @ApiModelProperty(example = "api-node-0", required = true, value = "")
  public String getFriendlyName() {
    return friendlyName;
  }

  public void setFriendlyName(String friendlyName) {
    this.friendlyName = friendlyName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeInfoDTO nodeInfoDTO = (NodeInfoDTO) o;
    return Objects.equals(this.publicKey, nodeInfoDTO.publicKey) &&
        Objects.equals(this.port, nodeInfoDTO.port) &&
        Objects.equals(this.networkIdentifier, nodeInfoDTO.networkIdentifier) &&
        Objects.equals(this.version, nodeInfoDTO.version) &&
        Objects.equals(this.roles, nodeInfoDTO.roles) &&
        Objects.equals(this.host, nodeInfoDTO.host) &&
        Objects.equals(this.friendlyName, nodeInfoDTO.friendlyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publicKey, port, networkIdentifier, version, roles, host, friendlyName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NodeInfoDTO {\n");

    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
    sb.append("    port: ").append(toIndentedString(port)).append("\n");
    sb.append("    networkIdentifier: ").append(toIndentedString(networkIdentifier)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("    host: ").append(toIndentedString(host)).append("\n");
    sb.append("    friendlyName: ").append(toIndentedString(friendlyName)).append("\n");
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