package io.github.ctlove0523.auth.jwt.apollo;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import io.github.ctlove0523.auth.jwt.core.SignKeyChangeHandler;
import io.github.ctlove0523.auth.jwt.core.SignKeyProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApolloSignKeyProvider implements SignKeyProvider {
    private String namespace;
    private String configKey;
    private List<SignKeyChangeHandler> handlers = new CopyOnWriteArrayList<>();

    public ApolloSignKeyProvider(String namespace, String configKey) {
        this.namespace = namespace;
        this.configKey = configKey;
        ConfigService.getConfig(namespace).addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent configChangeEvent) {
                handlers.forEach(handler -> {
                    String oldSignKey = configChangeEvent.getChange(configKey).getOldValue();
                    String newSignKey = configChangeEvent.getChange(configKey).getNewValue();
                    handler.handle(oldSignKey, newSignKey);
                });
            }
        }, new HashSet<>(Collections.singletonList(configKey)));
    }

    @Override
    public String getSignKey() {
        return ConfigService.getConfig(namespace).getProperty(configKey, "");
    }

    @Override
    public void registerHandler(SignKeyChangeHandler handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }
}
