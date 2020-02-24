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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsSame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CircularStackTest {

    @Test
    public void peekOnEmptyStackThrowsException() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> intStack.peek());
    }

    @Test
    public void popFromEmptyStackThrowsException() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        Assertions.assertThrows(IndexOutOfBoundsException.class, () ->intStack.pop());
    }

    @Test
    public void canAddSingleElementToCircularStack() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        intStack.push(123);

        // Assert:
        MatcherAssert.assertThat(intStack.peek(), IsEqual.equalTo(123));
        MatcherAssert.assertThat(intStack.size(), IsEqual.equalTo(1));
    }

    @Test
    public void canAddLimitElementsToCircularStack() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        intStack.push(555);
        intStack.push(777);
        intStack.push(888);

        // Assert:
        MatcherAssert.assertThat(intStack.peek(), IsEqual.equalTo(888));
        MatcherAssert.assertThat(intStack.size(), IsEqual.equalTo(3));
    }

    @Test
    public void addingMoreThanLimitElementsToCircularStackAgesOutOlderElements() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        for (int i = 0; i < 100; ++i) {
            intStack.push(123 + i);
        }

        // Assert:
        MatcherAssert.assertThat(intStack.peek(), IsEqual.equalTo(123 + 99));
        MatcherAssert.assertThat(intStack.size(), IsEqual.equalTo(3));
    }

    @Test
    public void poppingFromStackDecreasesSizeByOne() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        for (int i = 0; i < 100; ++i) {
            intStack.push(123 + i);
        }

        intStack.pop();

        // Assert:
        MatcherAssert.assertThat(intStack.peek(), IsEqual.equalTo(123 + 99 - 1));
        MatcherAssert.assertThat(intStack.size(), IsEqual.equalTo(2));
    }

    // region shallowCopyTo

    @Test
    public void canCopyLargerToSmaller() {
        // Arrange:
        final CircularStack<Integer> source = this.createStack(10);
        final CircularStack<Integer> destination = this.createStack(3);

        // Act:
        for (int i = 0; i < 10; ++i) {
            source.push(i);
        }

        source.shallowCopyTo(destination);

        // Assert:
        MatcherAssert.assertThat(source.size(), IsEqual.equalTo(10));
        MatcherAssert.assertThat(destination.size(), IsEqual.equalTo(3));
        int i = 7;
        for (final Integer element : destination) {
            MatcherAssert.assertThat(element, IsEqual.equalTo(i++));
        }
    }

    @Test
    public void canCopySmallerToLarger() {
        // Arrange:
        final CircularStack<Integer> source = this.createStack(3);
        final CircularStack<Integer> destination = this.createStack(10);

        // Act:
        for (int i = 0; i < 3; ++i) {
            source.push(i);
        }

        source.shallowCopyTo(destination);

        // Assert:
        MatcherAssert.assertThat(source.size(), IsEqual.equalTo(3));
        MatcherAssert.assertThat(destination.size(), IsEqual.equalTo(3));
        int i = 0;
        for (final Integer element : destination) {
            MatcherAssert.assertThat(element, IsEqual.equalTo(i++));
        }
    }

    @Test
    public void copyIsAShallowCopy() {
        // Arrange:
        final CircularStack<Integer> stack1 = this.createStack(3);
        final CircularStack<Integer> stack2 = this.createStack(3);

        // Act:
        for (int i = 0; i < 10; ++i) {
            stack1.push(i);
        }
        stack1.shallowCopyTo(stack2);

        // Assert:
        MatcherAssert.assertThat(stack1.size(), IsEqual.equalTo(3));
        MatcherAssert.assertThat(stack2.size(), IsEqual.equalTo(3));
        for (int i = 0; i < 3; ++i) {
            MatcherAssert.assertThat(stack1.peek(), IsSame.sameInstance(stack2.peek()));
            stack1.pop();
            stack2.pop();
        }
    }

    // endregion

    // region iteration

    @Test
    public void canIterateOverStackFromOldestToNewestElement() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        for (int i = 0; i < 3; ++i) {
            intStack.push(123 + i);
        }

        // Assert:
        int i = 123;
        for (final Integer elem : intStack) {
            MatcherAssert.assertThat(elem, IsEqual.equalTo(i));
            ++i;
        }
    }

    @Test
    public void canIterateOverStackFromNewestToOldestElementByPoppingAllElements() {
        // Arrange:
        final CircularStack<Integer> intStack = this.createStack(3);

        // Act:
        for (int i = 0; i < 3; ++i) {
            intStack.push(123 + i);
        }

        // Assert:
        for (int i = 125; i >= 123; --i) {
            MatcherAssert.assertThat(intStack.peek(), IsEqual.equalTo(i));
            intStack.pop();
        }

        MatcherAssert.assertThat(intStack.size(), IsEqual.equalTo(0));
    }

    // endregion

    private CircularStack<Integer> createStack(final int i) {
        return new CircularStack<>(i);
    }
}
