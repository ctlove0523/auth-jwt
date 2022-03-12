package io.github.ctlove0523.auth.jwt.consul;

import io.github.ctlove0523.auth.jwt.core.JacksonUtil;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Demo {
    public static void main(String[] args) {
        byte[] bytes = Base64.getDecoder().decode("eyJpYXQiOjE2NDcwOTQ1NTMsImV4cCI6MTY0NzE4MDk1MywiaXNzIjoiYXV0aC1qd3QiLCJJZGVudGl0eSI6IntcIklERU5USVRZX0lEXCI6XCIxMjNcIn0ifQ");
        System.out.println(new String(bytes));
    }
}
