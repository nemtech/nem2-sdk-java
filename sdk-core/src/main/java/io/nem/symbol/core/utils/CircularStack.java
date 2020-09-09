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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Circular stack is a last in first out buffer with fixed size that replace its oldest element if
 * full. The removal order is inverse of insertion order. The iteration order is the same as
 * insertion order. <strong>Note that implementation is not synchronized.</strong>
 *
 * @param <E> Type of elements on the stack.
 */
public class CircularStack<E> implements Iterable<E> {

  private final List<E> elements;
  private final int limit;

  /**
   * Creates circular stack with at most limit elements.
   *
   * @param limit Maximum number of elements on the stack.
   */
  public CircularStack(final int limit) {
    this.elements = new ArrayList<>(limit);
    this.limit = limit;
  }

  /**
   * Creates shallow copy in destination.
   *
   * @param destination CircularStack to which elements should be copied.
   */
  public void shallowCopyTo(final CircularStack<E> destination) {
    destination.elements.clear();
    destination.pushAll(this);
  }

  private void pushAll(final CircularStack<E> rhs) {
    int i = 0;
    for (final E element : rhs) {
      if (i >= rhs.size() - this.limit) {
        this.push(element);
      }

      ++i;
    }
  }

  /**
   * Adds element to the stack.
   *
   * @param element Element to be added.
   */
  public void push(final E element) {
    this.elements.add(element);
    if (this.elements.size() > this.limit) {
      this.elements.remove(0);
    }
  }

  /**
   * Gets most recently added element.
   *
   * @return Most recently added element.
   */
  public E peek() {
    return this.elements.get(this.elements.size() - 1);
  }

  /** Removes most recently added element. */
  public void pop() {
    this.elements.remove(this.elements.size() - 1);
  }

  /**
   * Returns size of a stack.
   *
   * @return Size of a stack (can be smaller than limit).
   */
  public int size() {
    return this.elements.size();
  }

  @Override
  public Iterator<E> iterator() {
    return this.elements.iterator();
  }
}
