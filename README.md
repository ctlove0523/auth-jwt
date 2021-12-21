# auth-jwt
auth-jwt提供基于JWT Token的认证能力，为了方便集成使用提供了开箱即用的插件，同时开放Token生成和校验能力，以方便基于auth-jwt开发其他功能。



# 1 快速开始

## 1.1 创建、校验Token

`TokenClient` 依赖`SignKeyProvider` 提供签名token的key，`IdentityVerifier`对token中的身份进行校验。



### 1.1.1 创建`SignKeyProvider`：

~~~java
SignKeyProvider signKeyProvider = new SignKeyProvider() {
	@Override
	public String getSignKey() {
		return UUID.randomUUID().toString();
	}

	@Override
	public void registerHandler(SignKeyChangeHandler handler) {
		// no op
	}
};
~~~

上面创建的`SignKeyProvider` 返回一个UUID作为key，并且不支持注册key变化处理器。



### 1.1.2 创建`IdentityVerifier`：

~~~java
IdentityVerifier identityVerifier = identity -> true;
~~~

上面创建了一个不对身份进行任何校验的`IdentityVerifier`



### 1.1.2 创建`TokenClient`：

~~~java
TokenClient tokenClient = TokenClient.newBuilder()
	.withSignKeyProvider(signKeyProvider)
	.withIdentityVerifier(identityVerifier)
	.build();
~~~

