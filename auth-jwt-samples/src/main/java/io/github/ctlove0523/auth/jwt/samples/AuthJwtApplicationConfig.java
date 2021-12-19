package io.github.ctlove0523.auth.jwt.samples;

import com.ecwid.consul.v1.ConsulClient;
import io.github.ctlove0523.auth.jwt.consul.ConsulSignKeyProvider;
import io.github.ctlove0523.auth.jwt.core.*;
import io.github.ctlove0523.auth.jwt.servlet.filter.AuthJwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
public class AuthJwtApplicationConfig {

    @Bean
    public ConsulClient createConsulClient() {
        return new ConsulClient();
    }

    @Bean
    public SignKeyProvider createSignKeyProvider(ConsulClient client) {
        return new ConsulSignKeyProvider(client);
    }

    @Bean
    public TokenClient clientTokenClient(SignKeyProvider signKeyProvider) {

        return new JwtTokenClient(signKeyProvider, new IdentityVerifier() {
            @Override
            public boolean validIdentity(Identity identity) {
                return true;
            }
        });
    }

    @Bean
    public FilterRegistrationBean<Filter> registerSecondBean(TokenClient tokenClient) {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new AuthJwtFilter(tokenClient));
        filter.setName("third filter");
        filter.setOrder(1);
        filter.setUrlPatterns(Arrays.asList("/application"));
        return filter;
    }

}
