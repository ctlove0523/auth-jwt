package io.github.ctlove0523.auth.jwt.core;

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

		public Builder withSignKeyProvider(SignKeyProvider signKeyProvider) {
			this.signKeyProvider = signKeyProvider;
			return this;
		}

		public Builder withIdentityVerifier(IdentityVerifier identityVerifier) {
			this.identityVerifier = identityVerifier;
			return this;
		}

		public Builder withWorkMod(WorkMod workMod) {
			this.workMod = workMod;
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
	}
}
