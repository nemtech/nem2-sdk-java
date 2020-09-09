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
package io.nem.symbol.core.math;
/** Represents a matrix element consisting of row, a column, and a value. */
public class MatrixElement {

  private final int row;
  private final int col;
  private final double value;

  /**
   * Creates a matrix entry
   *
   * @param row The row index.
   * @param col The column index.
   * @param value The value.
   */
  public MatrixElement(final int row, final int col, final double value) {
    this.row = row;
    this.col = col;
    this.value = value;
  }

  /**
   * Gets the row index.
   *
   * @return The row index.
   */
  public Integer getRow() {
    return this.row;
  }

  /**
   * Gets the column index.
   *
   * @return The column index.
   */
  public Integer getColumn() {
    return this.col;
  }

  /**
   * Gets the value.
   *
   * @return The value.
   */
  public Double getValue() {
    return this.value;
  }

  @Override
  public int hashCode() {
    return this.getRow() ^ this.getColumn();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof MatrixElement)) {
      return false;
    }

    final MatrixElement rhs = (MatrixElement) obj;
    return this.getRow().equals(rhs.getRow())
        && this.getColumn().equals(rhs.getColumn())
        && this.getValue().equals(rhs.getValue());
  }
}
