package io.github.ctlove0523.auth.jwt.servlet.filter;

import io.github.ctlove0523.auth.jwt.core.Constants;
import io.github.ctlove0523.auth.jwt.core.TokenCheckResult;
import io.github.ctlove0523.auth.jwt.core.TokenClient;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthJwtFilter implements Filter {
    private TokenClient tokenClient;

    public AuthJwtFilter(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String token = request.getHeader(Constants.TOKEN_KEY);
            if (token == null || token.isEmpty()) {
                response.setStatus(400);
                writeErrorMessage(response,"X-Auth-Jwt-Token not exist");
                return;
            }

            TokenCheckResult result = tokenClient.validToken(token);
            if (!result.isPass()) {
                response.setStatus(401);
                writeErrorMessage(response,"X-Auth-Jwt-Token invalid");
                return;
            }

            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void writeErrorMessage(HttpServletResponse response, String message) {
        try {
            response.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
