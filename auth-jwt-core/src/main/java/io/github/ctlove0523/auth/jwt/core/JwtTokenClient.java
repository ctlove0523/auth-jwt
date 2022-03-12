package io.github.ctlove0523.auth.jwt.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class JwtTokenClient implements TokenClient, SignKeyChangeHandler {
    private static final long TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L;
    private final SignKeyProvider signKeyProvider;
    private final IdentityVerifier identityVerifier;
    private final WorkMod workMod;

    private final Cache<String, String> createdTokenCache = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofHours(23L))
            .build();

    private final Cache<String, TokenCheckResult> checkedTokenCache = Caffeine
            .newBuilder()
            .expireAfter(new TokenCheckResultExpiry())
            .build();

    private final Cache<String, TokenCheckResult> lastCheckedTokenCache = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5L))
            .build();

    public JwtTokenClient(Builder builder) {
        this.signKeyProvider = builder.getSignKeyProvider();
        this.identityVerifier = builder.getIdentityVerifier();
        this.workMod = builder.getWorkMod();
    }

    @Override
    public String getToken(Identity identity) {
        Objects.requireNonNull(identity, "identity");
        String id = identity.getId();

        String token = createdTokenCache.getIfPresent(id);
        if (Objects.nonNull(token)) {
            return token;
        }

        token = createToken(identity);
        createdTokenCache.put(id, token);

        return token;
    }

    @Override
    public TokenCheckResult validToken(String token) {
        TokenCheckResult checkResult = checkedTokenCache.getIfPresent(token);
        if (Objects.nonNull(checkResult)) {
            return checkResult;
        }

        TokenCheckResult lastCheckResult = lastCheckedTokenCache.getIfPresent(token);
        if (Objects.nonNull(lastCheckResult)) {
            return lastCheckResult;
        }

        TokenCheckResult newCheckedResult = verifyToken(token, workMod);

        checkedTokenCache.put(token, newCheckedResult);
        return newCheckedResult;
    }

    private TokenCheckResult verifyToken(String token, WorkMod workMod) {
        switch (workMod) {
            case SharedKey:
                return verifyTokenWithKey(token, signKeyProvider.getSignKey());
            case ExclusiveKey:
                String[] tokenParts = token.split("\\.");
                String body = new String(Base64.getDecoder().decode(tokenParts[1]));
                System.out.println(body);

                String claims = body;
                Map<String, String> claimsMap = JacksonUtil.json2Pojo(claims, Map.class);
                String signKey = signKeyProvider.getSignKey(claimsMap.get("iss"));
                return verifyTokenWithKey(token, signKey);
            default:
                return new TokenCheckFailResult(new Exception("work mode invalid"));
        }
    }

    private TokenCheckResult verifyTokenWithKey(String token, String signKey) {
        TokenCheckResult tokenCheckedResult;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            String identityString = (String) claims.get("Identity");
            DefaultIdentity identity = JacksonUtil.json2Pojo(identityString, DefaultIdentity.class);
            if (identityVerifier.validIdentity(identity)) {
                tokenCheckedResult = new TokenCheckPassResult(claims.getExpiration(), identity);
            } else {
                tokenCheckedResult = new TokenCheckFailResult(new Exception("invalid"));
            }
        } catch (Exception e) {
            tokenCheckedResult = new TokenCheckFailResult(e);
        }

        return tokenCheckedResult;
    }

    @Override
    public boolean handle(String oldKey, String newKey) {
        System.out.println("sign key changed");
        ConcurrentMap<String, TokenCheckResult> tmp = checkedTokenCache.asMap();
        lastCheckedTokenCache.invalidateAll();
        lastCheckedTokenCache.putAll(tmp);

        checkedTokenCache.invalidateAll();
        createdTokenCache.invalidateAll();
        return true;
    }

    private String createToken(Identity identity) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + TOKEN_VALID_TIME);

        Claims claims = new DefaultClaims();
        claims.setIssuedAt(issuedAt);
        claims.setExpiration(expiration);
        claims.setIssuer(identity.getId());
        claims.put("Identity", JacksonUtil.pojo2Json(identity));
        String signKey = "";
        switch (workMod) {
            case SharedKey:
                signKey = signKeyProvider.getSignKey();
                break;
            case ExclusiveKey:
                signKey = signKeyProvider.getSignKey(identity.getId());
                break;
            default:
        }
        return Jwts.builder()
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    private static final class TokenCheckResultExpiry implements Expiry<String, TokenCheckResult> {

        @Override
        public long expireAfterCreate(@NonNull String key, @NonNull TokenCheckResult value, long currentTime) {
            return getExpire(value);
        }

        @Override
        public long expireAfterUpdate(@NonNull String key, @NonNull TokenCheckResult value, long currentTime, @NonNegative long currentDuration) {
            return getExpire(value);
        }

        @Override
        public long expireAfterRead(@NonNull String key, @NonNull TokenCheckResult value, long currentTime, @NonNegative long currentDuration) {
            return Long.MAX_VALUE;
        }

        private long getExpire(TokenCheckResult result) {
            if (!result.isPass()) {
                return TimeUnit.HOURS.toNanos(4L);
            }

            Date now = new Date();
            Date expire = result.getExpiration();

            return TimeUnit.MILLISECONDS.toNanos(expire.getTime() - now.getTime());
        }
    }
}
