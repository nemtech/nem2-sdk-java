/*
 * Copyright 2018 NEM
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

package io.nem.core.test;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;

/**
 * Helper class that contains functions for asserting that specific exceptions are thrown.
 */
public class ExceptionAssert {

    /**
     * Asserts that the execution of consumer throws an exception of the specific class.
     *
     * @param consumer The consumer.
     * @param exceptionClass The expected exception class.
     */
    public static void assertThrows(final Consumer<Void> consumer, final Class<?> exceptionClass) {
        assertThrows(consumer, exceptionClass, ex -> {
        });
    }

    /**
     * Asserts that the execution of consumer throws an exception of the specific class.
     *
     * @param consumer The consumer.
     * @param exceptionClass The expected exception class.
     * @param message The custom assertion message.
     */
    public static void assertThrows(
        final Consumer<Void> consumer, final Class<?> exceptionClass, final String message) {
        assertThrows(consumer, exceptionClass, ex -> {
        }, message);
    }

    /**
     * Asserts that the execution of consumer throws an exception of the specific class.
     *
     * @param consumer The consumer.
     * @param exceptionClass The expected exception class.
     * @param assertExceptionProperties Consumer that is passed the matching exception to run any
     * additional validation.
     */
    public static <T> void assertThrows(
        final Consumer<Void> consumer,
        final Class<T> exceptionClass,
        final Consumer<T> assertExceptionProperties) {
        assertThrows(consumer, exceptionClass, assertExceptionProperties, null);
    }

    /**
     * Asserts that the execution of consumer throws an exception of the specific class.
     *
     * @param consumer The consumer.
     * @param exceptionClass The expected exception class.
     * @param assertExceptionProperties Consumer that is passed the matching exception to run any
     * additional validation.
     * @param message The custom assertion message.
     */
    @SuppressWarnings("unchecked")
    private static <T> void assertThrows(
        final Consumer<Void> consumer,
        final Class<T> exceptionClass,
        final Consumer<T> assertExceptionProperties,
        final String message) {
        final String normalizedMessage = null == message ? "" : String.format("[%s]: ", message);
        try {
            consumer.accept(null);
        } catch (final Exception ex) {
            if (exceptionClass.isAssignableFrom(ex.getClass())) {
                assertExceptionProperties.accept((T) ex);
                return;
            }

            Assertions.fail(
                String.format(
                    "%sunexpected exception of type %s was thrown: '%s'",
                    normalizedMessage, ex.getClass(), ex.getMessage()));
        }

        Assertions.fail(
            String.format(
                "%sexpected exception of type %s was not thrown", normalizedMessage,
                exceptionClass));
    }

    /**
     * Asserts that the execution of consumer throws a completion exception wrapping an exception of
     * the specific class.
     *
     * @param consumer The consumer.
     * @param exceptionClass The expected exception class.
     */
    public static void assertThrowsCompletionException(
        final Consumer<Void> consumer, final Class<?> exceptionClass) {
        try {
            consumer.accept(null);
        } catch (final CompletionException completionEx) {
            final Throwable ex = completionEx.getCause();
            if (ex.getClass() == exceptionClass) {
                return;
            }

            Assertions.fail(String.format("unexpected exception of type %s was thrown", ex.getClass()));
        }

        Assertions.fail(String.format("expected exception of type %s was not thrown", exceptionClass));
    }
}
