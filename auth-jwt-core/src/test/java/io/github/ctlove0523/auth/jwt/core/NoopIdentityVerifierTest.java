package io.github.ctlove0523.auth.jwt.core;

import org.junit.Assert;
import org.junit.Test;

public class NoopIdentityVerifierTest {

    @Test
    public void testValidIdentity_should_returnTure() {
        IdentityVerifier verifier = new NoopIdentityVerifier();
        Identity identity = Identity.newIdentity().setId("id for test");

        Assert.assertTrue(verifier.validIdentity(identity));
    }
}
