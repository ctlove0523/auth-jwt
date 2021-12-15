package io.github.ctlove0523.auth.jwt.core;

import java.util.Map;

/**
 * 用户或者组件的身份
 * @author chentong
 */
public interface Identity extends Map<String, Object> {

	String IDENTITY_ID = "IDENTITY_ID";

	Identity setId(String id);

	Identity setValue(String key, Object val);

	/**
	 * 身份的唯一标识，不同身份的id不同
	 * @return 身份ID
	 */
	String getId();
}
