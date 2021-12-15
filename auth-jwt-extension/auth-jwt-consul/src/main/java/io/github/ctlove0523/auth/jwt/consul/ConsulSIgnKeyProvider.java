package io.github.ctlove0523.auth.jwt.consul;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsulSIgnKeyProvider implements SignKeyProvider {
    private ConsulClient consulClient;
    private String configKey;
    private List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();

    public ConsulSIgnKeyProvider(ConsulClient consulClient) {
        this(consulClient, "jwt.key");
    }

    public ConsulSIgnKeyProvider(ConsulClient consulClient, String configKey) {
        Objects.requireNonNull(consulClient, "consulClient");
        Objects.requireNonNull(configKey, "configKey");
        this.consulClient = consulClient;
        this.configKey = configKey;
    }

    @Override
    public String getSignKey() {
        return null;
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }
}
