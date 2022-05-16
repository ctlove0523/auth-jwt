package io.github.ctlove0523.auth.jwt.core;

import java.util.Objects;

public interface TokenClient {

	static Builder newBuilder() {
		return new Builder();
	}

	String getToken(Identity identity);

	TokenCheckResult validToken(String token);

	final class Builder {
		private SignKeyProvider signKeyProvider;
		private IdentityVerifier identityVerifier;
		private WorkMod workMod;
		private TokenCipher tokenCipher;

		public Builder withSignKeyProvider(SignKeyProvider signKeyProvider) {
			Objects.requireNonNull(signKeyProvider, "signKeyProvider");
			this.signKeyProvider = signKeyProvider;
			return this;
		}

		public Builder withIdentityVerifier(IdentityVerifier identityVerifier) {
			Objects.requireNonNull(identityVerifier, "identityVerifier");
			this.identityVerifier = identityVerifier;
			return this;
		}

		public Builder withWorkMod(WorkMod workMod) {
			Objects.requireNonNull(workMod, "workMod");
			this.workMod = workMod;
			return this;
		}

		public Builder withTokenCipher(TokenCipher tokenCipher) {
			Objects.requireNonNull(tokenCipher, "tokenCipher");
			this.tokenCipher = tokenCipher;
			return this;
		}

		public TokenClient build() {
			return new JwtTokenClient(this);
		}

		public SignKeyProvider getSignKeyProvider() {
			return signKeyProvider;
		}

		public IdentityVerifier getIdentityVerifier() {
			return identityVerifier;
		}

		public WorkMod getWorkMod() {
			return workMod;
		}

		public TokenCipher getTokenCipher() {
			return tokenCipher;
		}
	}
}
