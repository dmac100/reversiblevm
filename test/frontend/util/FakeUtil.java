package frontend.util;

import java.lang.reflect.Method;

import org.mockito.cglib.proxy.Callback;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.objenesis.ObjenesisHelper;

/**
 * Utilities to create fake objects for unit testing.
 */
public class FakeUtil {
	/**
	 * Returns an object of class 'classToMock' which delegates to the
	 * MethodInterceptor. Avoids calling the constructor of classToMock but final
	 * methods call the original object.
	 */
	public static <T> T createProxy(final Class<?> classToMock, final MethodInterceptor interceptor) {
		final Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(classToMock);
		enhancer.setCallbackType(interceptor.getClass());
		final Class<?> proxyClass = enhancer.createClass();
		Enhancer.registerCallbacks(proxyClass, new Callback[] { interceptor });
		return (T) ObjenesisHelper.newInstance(proxyClass);
	}

	/**
	 * Returns an object of class 'classToMock' which delegates its implementation
	 * to the methods in 'delegate'. Avoids calling the constructor of classToMock but final
	 * methods call the original object.
	 */
	public static <T> T createDelegate(final Class<T> classToMock, final Object delegate) {
		return createProxy(classToMock, new MethodInterceptor() {
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Method m = delegate.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
				m.setAccessible(true);
				return m.invoke(delegate, args);
			}
		});
	}
}
