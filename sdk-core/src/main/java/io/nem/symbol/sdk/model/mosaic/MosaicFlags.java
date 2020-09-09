/*
 * Copyright 2020 NEM
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
package io.nem.symbol.sdk.model.mosaic;
/**
 * The mosaic flags structure describes mosaic flags.
 *
 * @since 1.0
 */
public class MosaicFlags {

  /**
   * The creator can choose between a definition that allows a mosaic supply change at a later point
   * or an immutable supply. Allowed values for the property are "true" and "false". The default
   * value is "false".
   */
  private final boolean supplyMutable;
  /**
   * The creator can choose if the mosaic definition should allow for transfers of the mosaic among
   * accounts other than the creator. If the property 'transferable' is set to "false", only
   * transfer transactions having the creator as sender or as recipient can transfer mosaics of that
   * type. If set to "true" the mosaics can be transferred to and from arbitrary accounts. Allowed
   * values for the property are thus "true" and "false". The default value is "true".
   */
  private final boolean transferable;
  /**
   * Not all the mosaics of a given network will be subject to mosaic restrictions. The feature will
   * only affect those to which the issuer adds the "restrictable" property explicitly at the moment
   * of its creation. This property appears disabled by default, as it is undesirable for autonomous
   * tokens like the public network currency.
   */
  private final boolean restrictable;

  private MosaicFlags(
      final boolean supplyMutable, final boolean transferable, final boolean restrictable) {
    this.supplyMutable = supplyMutable;
    this.transferable = transferable;
    this.restrictable = restrictable;
  }

  /**
   * Creates a mosaic from the configurable byte value.
   *
   * @param flags the flags.
   * @return Mosaic flags.
   */
  public static MosaicFlags create(int flags) {
    String flagsString = "00" + Integer.toBinaryString(flags);
    String bitMapFlags = flagsString.substring(flagsString.length() - 3);
    return MosaicFlags.create(
        bitMapFlags.charAt(2) == '1', bitMapFlags.charAt(1) == '1', bitMapFlags.charAt(0) == '1');
  }

  /**
   * Creates a mosaic flags.
   *
   * @param supplyMutable True supply can change.
   * @param transferable True mosaic can be transfer.
   * @param restrictable True mosaic supports restrictions.
   * @return Mosaic flags.
   */
  public static MosaicFlags create(
      boolean supplyMutable, boolean transferable, boolean restrictable) {
    return new MosaicFlags(supplyMutable, transferable, restrictable);
  }

  /**
   * Creates a mosaic flags.
   *
   * @param supplyMutable True supply can change.
   * @param transferable True mosaic can be transfer.
   * @return Mosaic flags.
   */
  public static MosaicFlags create(boolean supplyMutable, boolean transferable) {
    return new MosaicFlags(supplyMutable, transferable, false);
  }

  /**
   * Returns true if supply is mutable
   *
   * @return if supply is mutable
   */
  public boolean isSupplyMutable() {
    return supplyMutable;
  }

  /**
   * Returns true if mosaic is transferable between non-owner accounts
   *
   * @return if the mosaic is transferable between non-owner accounts
   */
  public boolean isTransferable() {
    return transferable;
  }

  /**
   * Returns true if mosaic is restrictable
   *
   * @return if mosaic is restrictable
   */
  public boolean isRestrictable() {
    return restrictable;
  }

  /**
   * Gets the consolidated mosaic flags value.
   *
   * @return the merged flags in a int.
   */
  public int getValue() {
    return (this.supplyMutable ? 1 : 0) + (this.transferable ? 2 : 0) + (this.restrictable ? 4 : 0);
  }
}
