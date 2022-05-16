package io.github.ctlove0523.auth.jwt.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JwtTokenClient implements TokenClient, SignKeyChangeHandler {
    private static final long TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L;
    private final SignKeyProvider signKeyProvider;
    private final IdentityVerifier identityVerifier;
    private final TokenCipher tokenCipher;

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

        if (builder.getTokenCipher() != null) {
            this.tokenCipher = builder.getTokenCipher();
        } else {
            this.tokenCipher = new NoopTokenCipher();
        }
    }

    @Override
    public String getToken(Identity identity) {
        Objects.requireNonNull(identity, "identity");
        String id = identity.getId();

        String token = createdTokenCache.getIfPresent(id);
        if (Objects.nonNull(token)) {
            return tokenCipher.decrypt(token);
        }

        token = createToken(identity);
        createdTokenCache.put(id, tokenCipher.encrypt(token));

        return token;
    }

    @Override
    public TokenCheckResult validToken(String token) {
        String tokenHash = tokenCipher.hashCode(token);
        TokenCheckResult checkResult = checkedTokenCache.getIfPresent(tokenHash);
        if (Objects.nonNull(checkResult)) {
            return checkResult;
        }

        TokenCheckResult lastCheckResult = lastCheckedTokenCache.getIfPresent(tokenHash);
        if (Objects.nonNull(lastCheckResult)) {
            return lastCheckResult;
        }

        TokenCheckResult newCheckedResult = verifyToken(token);

        checkedTokenCache.put(tokenHash, newCheckedResult);
        return newCheckedResult;
    }

    private TokenCheckResult verifyToken(String token) {
        String[] tokenParts = token.split("\\.");
        String body = new String(Base64.getDecoder().decode(tokenParts[1]));
        Map<String, String> claims = JacksonUtil.json2Map(body);
        String identityString = claims.get("Identity");

        DefaultIdentity identity = JacksonUtil.json2Pojo(identityString, DefaultIdentity.class);
        return verifyTokenWithKey(token, signKeyProvider.getVerifyKey(identity.getId()));
    }

    private TokenCheckResult verifyTokenWithKey(String token, Key signKey) {
        TokenCheckResult tokenCheckedResult;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signKey)
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
    public boolean handle(SignKeyChangeEvent event) {
        String id = event.getOwner();
        createdTokenCache.invalidate(id);

        TokenCheckResult result = checkedTokenCache.getIfPresent(id);
        if (Objects.nonNull(result)) {
            lastCheckedTokenCache.put(id, result);
        }

        checkedTokenCache.invalidate(id);
        checkedTokenCache.invalidate(id);

        return true;
    }

    private String createToken(Identity identity) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + TOKEN_VALID_TIME);

        Claims claims = Jwts.claims();
        claims.setIssuedAt(issuedAt);
        claims.setExpiration(expiration);
        claims.setIssuer(identity.getId());
        claims.put("Identity", JacksonUtil.pojo2Json(identity));

        Key secretKey = getSignKey(identity);
        return Jwts.builder()
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 获取签名token的Key
     *
     * @return {@see Key}
     */
    private Key getSignKey(Identity identity) {
        return signKeyProvider.getSignKey(identity.getId());
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
