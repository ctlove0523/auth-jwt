package io.github.ctlove0523.auth.jwt.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class TokenClientTest {
    private static final String DEFAULT_SIGN_KEY = UUID.randomUUID().toString();
    private SignKeyProvider signKeyProvider = new SignKeyProvider() {
        @Override
        public String getSignKey() {
            return DEFAULT_SIGN_KEY;
        }

        @Override
        public void registerHandler(SignKeyChangeHandler handler) {
            // no op
        }
    };

    @Test
    public void testGetToken_return_tokenString_when_identityValid() {
        IdentityVerifier identityVerifier = identity -> true;

        TokenClient tokenClient = TokenClient.newBuilder()
                .withSignKeyProvider(signKeyProvider)
                .withIdentityVerifier(identityVerifier)
                .build();

        Identity identity = Identity.newIdentity().setId(UUID.randomUUID().toString());

        String token = tokenClient.getToken(identity);

        Assert.assertNotNull(token);
    }

    @Test
    public void testGetToken_return_sameString_when_repeatedCall() {
        IdentityVerifier identityVerifier = identity -> true;

        TokenClient tokenClient = TokenClient.newBuilder()
                .withSignKeyProvider(signKeyProvider)
                .withIdentityVerifier(identityVerifier)
                .build();

        Identity identity = Identity.newIdentity().setId(UUID.randomUUID().toString());

        String token = tokenClient.getToken(identity);

        String secondToken = tokenClient.getToken(identity);

        Assert.assertEquals(token, secondToken);
    }

    @Test
    public void testValidToken_return_passResult_when_tokenValid() {
        IdentityVerifier identityVerifier = identity -> true;

        TokenClient tokenClient = TokenClient.newBuilder()
                .withSignKeyProvider(signKeyProvider)
                .withIdentityVerifier(identityVerifier)
                .build();

        String id = UUID.randomUUID().toString();
        Identity identity = Identity.newIdentity().setId(id);

        String token = tokenClient.getToken(identity);

        TokenCheckResult result = tokenClient.validToken(token);
        Assert.assertTrue(result.isPass());

        Identity tokenIdentity = result.getIdentity();
        Assert.assertEquals(id, tokenIdentity.getId());
    }
}
