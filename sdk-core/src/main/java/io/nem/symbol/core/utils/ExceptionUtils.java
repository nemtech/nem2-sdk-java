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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Static class that contains helper functions for dealing with exceptions.
 */
public class ExceptionUtils {

    /**
     * Private constructor for this utility class.
     */
    private ExceptionUtils() {
    }

    /**
     * Propagates checked exceptions as a runtime exception.
     *
     * @param runnable The checked runnable.
     */
    public static void propagateVoid(final CheckedRunnable runnable) {
        propagateVoid(runnable, RuntimeException::new);
    }

    /**
     * Propagates checked exceptions as a specific runtime exception.
     *
     * @param runnable The checked runnable.
     * @param wrap A function that wraps an exception in a runtime exception.
     * @param <E> The specific exception type.
     */
    public static <E extends RuntimeException> void propagateVoid(
        final CheckedRunnable runnable, final Function<Exception, E> wrap) {
        propagate(new CheckedRuntimeToCallableAdapter(runnable), wrap);
    }

    /**
     * Propagates checked exceptions as a runtime exception.
     *
     * @param callable The function.
     * @param <T> The function return type.
     * @return The function result.
     */
    public static <T> T propagate(final Callable<T> callable) {
        return propagate(callable, RuntimeException::new);
    }

    /**
     * Propagates checked exceptions as a specific runtime exception.
     *
     * @param callable The function.
     * @param wrap A function that wraps an exception in a runtime exception.
     * @param <T> The function return type.
     * @param <E> The specific exception type.
     * @return The function result.
     */
    public static <T, E extends RuntimeException> T propagate(
        final Callable<T> callable, final Function<Exception, E> wrap) {
        try {
            return callable.call();
        } catch (final ExecutionException e) {
            if (RuntimeException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            }
            throw wrap.apply(e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (final Exception e) {
            throw wrap.apply(e);
        }
    }

    /**
     * Interface that mimics Runnable but can additionally throw checked exceptions.
     */
    public interface CheckedRunnable {

        /**
         * Executes the runnable.
         *
         * @throws Exception Any exception.
         */
        @SuppressWarnings("squid:S00112")
        void call() throws Exception;
    }

    private static class CheckedRuntimeToCallableAdapter implements Callable<Void> {

        private final CheckedRunnable runnable;

        public CheckedRuntimeToCallableAdapter(final CheckedRunnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public Void call() throws Exception {
            this.runnable.call();
            return null;
        }
    }
}
