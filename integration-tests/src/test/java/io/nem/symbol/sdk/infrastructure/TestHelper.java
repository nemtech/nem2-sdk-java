package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.ExceptionUtils;
import io.reactivex.Observable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestHelper {

    private final Config config;

    public TestHelper() {
        this.config = new Config();
        System.out.println("Running tests against server: " + config().getApiUrl());

    }

    public Config config() {
        return config;
    }

    /**
     * An utility method that executes a rest call though the Observable. It simplifies and unifies the executions of
     * rest calls.
     *
     * This methods adds the necessary timeouts and exception handling,
     *
     * @param observable the observable, typically the one that performs a rest call.
     * @param <T> the observable type
     * @return the response from the rest call.
     */
    public <T> T get(Observable<T> observable) {
        return get(observable.toFuture());
    }

    /**
     * An utility method that executes a rest call though the Observable. It simplifies and unifies the executions of
     * rest calls.
     *
     * This methods adds the necessary timeouts and exception handling,
     *
     * @param future the future, typically the one that performs a rest call.
     * @param <T> the future type
     * @return the response from the rest call.
     */
    public <T> T get(Future<T> future) {
        return ExceptionUtils
            .propagate(() -> future.get(config.getTimeoutSeconds(), TimeUnit.SECONDS));
    }

}
