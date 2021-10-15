package ys.cloud.sbot;

import java.lang.reflect.Method;
import java.util.Arrays;

public class PrivateMethod {

	static public Object invokeByName(Object subject, String name, Object... params) {
		try {
			Method method = findByName(subject.getClass(), name);
			method.setAccessible(true);
			return method.invoke(subject, params);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Method findByName(Class<?> class1, String name) {
		return Arrays.asList(class1.getDeclaredMethods()).stream().filter(m -> m.getName().equals(name)).findFirst()
				.get();
	}

	static public Object invoke(Object subject, String name, Object... params) {

		try {
			Class<?>[] parameterTypes = Arrays.stream(params).map(Object::getClass).toArray(Class<?>[]::new);
			Method method = subject.getClass().getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method.invoke(subject, params);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
