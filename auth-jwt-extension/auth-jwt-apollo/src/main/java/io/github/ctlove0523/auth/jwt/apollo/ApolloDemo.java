package io.github.ctlove0523.auth.jwt.apollo;

import com.ctrip.framework.apollo.ConfigService;

public class ApolloDemo {
    public static void main(String[] args) {
        String key = ConfigService.getAppConfig().getProperty("auth.jwt.key", "not get");
        System.out.println(key);
    }
}
