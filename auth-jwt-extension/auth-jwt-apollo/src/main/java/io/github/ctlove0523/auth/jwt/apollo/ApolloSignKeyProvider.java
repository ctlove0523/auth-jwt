package io.github.ctlove0523.auth.jwt.apollo;

import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApolloSignKeyProvider implements SignKeyProvider {
    private final String namespace;
    private final String configKey;
    private final List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();

    public ApolloSignKeyProvider(String namespace, String configKey) {
        this.namespace = namespace;
        this.configKey = configKey;
    }

    @Override
    public Key getSignKey(String identity) {
        // TODO: 2022/3/13
        return null;
    }

    @Override
    public Key getVerifyKey(String identity) {
        // TODO: 2022/3/13
        return null;
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }
}
