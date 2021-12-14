package io.github.ctlove0523.auth.jwt.core;

public class Demo {
	public static void main(String[] args) {
		SignKeyProvider signKeyProvider = new SignKeyProvider() {
			@Override
			public String getSignKey() {
				return "HELLO";
			}

			@Override
			public void registerHandler(SignKeyChangeHandler handler) {

			}
		};

		IdentityVerifier identityProvider = new IdentityVerifier() {
			@Override
			public boolean validIdentity(Identity identity) {
				return true;
			}
		};
		TokenClient tokenClient = new JwtTokenClient(signKeyProvider, identityProvider);

		Identity identity = new DefaultIdentity();
		identity.setId("123");
		String token = tokenClient.getToken(identity);

		System.out.println(token);

		System.out.println(tokenClient.validToken(token).pass());

	}
}
