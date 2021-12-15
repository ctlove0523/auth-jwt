package io.github.ctlove0523.auth.jwt.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AuthJwtApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthJwtApplication.class, args);
    }
}
