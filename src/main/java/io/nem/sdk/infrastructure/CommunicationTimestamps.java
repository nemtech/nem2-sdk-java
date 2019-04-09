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
 * CommunicationTimestamps
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-28T19:50:06.335-03:00")
public class CommunicationTimestamps {
  @SerializedName("sendTimestamp")
  private UInt64DTO sendTimestamp = null;

  @SerializedName("receiveTimestamp")
  private UInt64DTO receiveTimestamp = null;

  public CommunicationTimestamps sendTimestamp(UInt64DTO sendTimestamp) {
    this.sendTimestamp = sendTimestamp;
    return this;
  }

   /**
   * Get sendTimestamp
   * @return sendTimestamp
  **/
  @ApiModelProperty(value = "")
  public UInt64DTO getSendTimestamp() {
    return sendTimestamp;
  }

  public void setSendTimestamp(UInt64DTO sendTimestamp) {
    this.sendTimestamp = sendTimestamp;
  }

  public CommunicationTimestamps receiveTimestamp(UInt64DTO receiveTimestamp) {
    this.receiveTimestamp = receiveTimestamp;
    return this;
  }

   /**
   * Get receiveTimestamp
   * @return receiveTimestamp
  **/
  @ApiModelProperty(value = "")
  public UInt64DTO getReceiveTimestamp() {
    return receiveTimestamp;
  }

  public void setReceiveTimestamp(UInt64DTO receiveTimestamp) {
    this.receiveTimestamp = receiveTimestamp;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommunicationTimestamps communicationTimestamps = (CommunicationTimestamps) o;
    return Objects.equals(this.sendTimestamp, communicationTimestamps.sendTimestamp) &&
        Objects.equals(this.receiveTimestamp, communicationTimestamps.receiveTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sendTimestamp, receiveTimestamp);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommunicationTimestamps {\n");

    sb.append("    sendTimestamp: ").append(toIndentedString(sendTimestamp)).append("\n");
    sb.append("    receiveTimestamp: ").append(toIndentedString(receiveTimestamp)).append("\n");
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

