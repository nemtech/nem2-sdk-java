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

import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;

public class MatrixElementTest {

  private static final Map<String, MatrixElement> DESC_TO_ELEMENT_MAP =
      new HashMap<String, MatrixElement>() {
        {
          this.put("default", new MatrixElement(5, 4, 7.0));
          this.put("diff-row", new MatrixElement(6, 4, 7.0));
          this.put("diff-col", new MatrixElement(5, 3, 7.0));
          this.put("diff-val", new MatrixElement(5, 4, 7.2));
        }
      };

  // region equals / hashCode

  @Test
  public void canCreateMatrixElement() {
    // Act:
    final MatrixElement element = new MatrixElement(5, 3, 2.34);

    // Assert:
    MatcherAssert.assertThat(element.getRow(), IsEqual.equalTo(5));
    MatcherAssert.assertThat(element.getColumn(), IsEqual.equalTo(3));
    MatcherAssert.assertThat(element.getValue(), IsEqual.equalTo(2.34));
  }

  @Test
  public void equalsOnlyReturnsTrueForEquivalentObjects() {
    // Arrange:
    final MatrixElement element = new MatrixElement(5, 4, 7.0);

    // Assert:
    MatcherAssert.assertThat(DESC_TO_ELEMENT_MAP.get("default"), IsEqual.equalTo(element));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-row"), IsNot.not(IsEqual.equalTo(element)));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-col"), IsNot.not(IsEqual.equalTo(element)));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-val"), IsNot.not(IsEqual.equalTo(element)));
    MatcherAssert.assertThat(null, IsNot.not(IsEqual.equalTo(element)));
    MatcherAssert.assertThat(5, IsNot.not(IsEqual.equalTo(element)));
  }

  @Test
  public void hashCodesAreEqualForEquivalentObjects() {
    // Arrange:
    final MatrixElement element = new MatrixElement(5, 4, 7.0);
    final int hashCode = element.hashCode();

    // Assert:
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("default").hashCode(), IsEqual.equalTo(hashCode));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-row").hashCode(), IsNot.not(IsEqual.equalTo(hashCode)));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-col").hashCode(), IsNot.not(IsEqual.equalTo(hashCode)));
    MatcherAssert.assertThat(
        DESC_TO_ELEMENT_MAP.get("diff-val").hashCode(), IsEqual.equalTo(hashCode));
  }

  // endregion
}
