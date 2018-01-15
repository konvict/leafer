package org.ziwenxie.leafer.redis;

import java.lang.reflect.Method;
import java.util.Set;

public interface InvocationRegistry {

	void registerInvocation(Object invokedBean, Method invokedMethod,
							Object[] invocationArguments, Set<String> cacheNames);

}