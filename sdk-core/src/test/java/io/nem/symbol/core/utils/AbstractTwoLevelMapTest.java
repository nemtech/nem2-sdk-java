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
package io.nem.symbol.core.utils;

import io.nem.symbol.core.test.IsEquivalent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.junit.jupiter.api.Test;

public class AbstractTwoLevelMapTest {

  @Test
  public void previouslyUnknownValueCanBeRetrieved() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Act:
    final MockValue value = map.getItem("foo", "bar");

    // Assert:
    MatcherAssert.assertThat(value, IsNull.notNullValue());
  }

  @Test
  public void sameValueIsReturnedForSameKeys() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Act:
    final MockValue value1 = map.getItem("foo", "bar");
    final MockValue value2 = map.getItem("foo", "bar");

    // Assert:
    MatcherAssert.assertThat(value2, IsSame.sameInstance(value1));
  }

  @Test
  public void valueIsDirectional() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Act:
    final MockValue value1 = map.getItem("foo", "bar");
    final MockValue value2 = map.getItem("bar", "foo");

    // Assert:
    MatcherAssert.assertThat(value2, IsNot.not(IsSame.sameInstance(value1)));
  }

  @Test
  public void previouslyUnknownValueMapCanBeRetrieved() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Act:
    final Map<String, MockValue> values = map.getItems("foo");

    // Assert:
    MatcherAssert.assertThat(values, IsNull.notNullValue());
  }

  @Test
  public void getItemAndGetItemsReturnSameValueForSameKeys() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Act:
    final MockValue value1 = map.getItem("foo", "bar");
    final MockValue value2 = map.getItems("foo").get("bar");

    // Assert:
    MatcherAssert.assertThat(value2, IsSame.sameInstance(value1));
  }

  @Test
  public void removeRemovesKeyFromMap() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();
    map.getItems("foo");
    map.getItems("bar");
    map.getItems("baz");

    // Sanity:
    MatcherAssert.assertThat(
        map.keySet(), IsEquivalent.equivalentTo(Arrays.asList("foo", "bar", "baz")));

    // Act:
    map.remove("bar");

    // Assert:
    MatcherAssert.assertThat(map.keySet(), IsEquivalent.equivalentTo(Arrays.asList("foo", "baz")));
  }

  @Test
  public void removeDoesNothingIfKeyIsNotPresentInMap() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();
    map.getItems("foo");
    map.getItems("bar");
    map.getItems("baz");

    // Sanity:
    MatcherAssert.assertThat(
        map.keySet(), IsEquivalent.equivalentTo(Arrays.asList("foo", "bar", "baz")));

    // Act:
    map.remove("qux");
    map.remove("alice");

    // Assert:
    MatcherAssert.assertThat(
        map.keySet(), IsEquivalent.equivalentTo(Arrays.asList("foo", "bar", "baz")));
  }

  @Test
  public void keySetReturnsCollectionOfAllKeys() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();
    map.getItems("foo");
    map.getItems("bar");
    map.getItems("baz");

    // Assert:
    MatcherAssert.assertThat(
        map.keySet(), IsEquivalent.equivalentTo(Arrays.asList("foo", "bar", "baz")));
  }

  @Test
  public void keySetReturnsEmptyCollectionIfMapIsEmpty() {
    // Arrange:
    final AbstractTwoLevelMap<String, MockValue> map = new MockTwoLevelMap();

    // Assert:
    MatcherAssert.assertThat(map.keySet(), IsEquivalent.equivalentTo(Collections.emptyList()));
  }

  private static class MockValue {}

  private static class MockTwoLevelMap extends AbstractTwoLevelMap<String, MockValue> {

    @Override
    protected MockValue createValue() {
      return new MockValue();
    }
  }
}
