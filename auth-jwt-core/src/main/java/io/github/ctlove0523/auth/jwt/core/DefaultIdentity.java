package io.github.ctlove0523.auth.jwt.core;

import java.util.Map;

public class DefaultIdentity extends IdentityMap implements Identity {
	public DefaultIdentity() {
		super();
	}

	public DefaultIdentity(Map<String, Object> data) {
		super(data);
	}

	@Override
	public Identity setId(String id) {
		put(Identity.IDENTITY_ID, id);
		return this;
	}

	@Override
	public Identity setValue(String key, Object val) {
		put(key, val);
		return this;
	}

	@Override
	public String getId() {
		return getString(IDENTITY_ID);
	}
}
