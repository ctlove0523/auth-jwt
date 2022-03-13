package io.github.ctlove0523.auth.jwt.core;

public interface SignKeyChangeHandler {
    boolean handle(SignKeyChangeEvent event);
}
