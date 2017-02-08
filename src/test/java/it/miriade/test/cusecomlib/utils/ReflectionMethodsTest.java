package it.miriade.test.cusecomlib.utils;

/**
 * Test the speed of various methods for getting the caller class name
 */
public class ReflectionMethodsTest {

	/**
	 * Test all four methods
	 */
	public static void main(String[] args) {
		showResults(new ReflectionMethod());
		testPerformance(new ReflectionMethod());

		showResults(new ThreadStackTraceMethod());
		testPerformance(new ThreadStackTraceMethod());

		showResults(new ThrowableStackTraceMethod());
		testPerformance(new ThrowableStackTraceMethod());

		showResults(new SecurityManagerMethod());
		testPerformance(new SecurityManagerMethod());
	}

	private static void testPerformance(GetCallerClassNameMethod method) {
		long startTime = System.nanoTime();
		for (int i = 0; i < 1000000; i++) {
			method.getCallerClassName(2);
		}
		System.out.println("Elapsed:         " + ((double) (System.nanoTime() - startTime)) / 1000000 + " ms.");
	}

	private static void showResults(GetCallerClassNameMethod method) {
		String className = null;
		System.out.println("MethodName:      " + method.getMethodName());
		for (int i = 1; i < 5; i++) {
			className = method.getCallerClassName(i);
			System.out.println("CallerClassName: " + i + " " + className);
		}
	}

	/**
	 * Abstract class for testing different methods of getting the caller class name
	 */
	private static interface GetCallerClassNameMethod {
		public abstract String getCallerClassName(int callStackDepth);

		public abstract String getMethodName();
	}

	/**
	 * Uses the internal Reflection class
	 */
	private static class ReflectionMethod implements GetCallerClassNameMethod {
		public String getCallerClassName(int callStackDepth) {
			if (sun.reflect.Reflection.getCallerClass(callStackDepth) == null)
				return "Null caller";
			return sun.reflect.Reflection.getCallerClass(callStackDepth).getName();
		}

		public String getMethodName() {
			return "Reflection";
		}
	}

	/**
	 * Get a stack trace from the current thread
	 */
	private static class ThreadStackTraceMethod implements GetCallerClassNameMethod {
		public String getCallerClassName(int callStackDepth) {
			if (Thread.currentThread().getStackTrace().length <= callStackDepth)
				return "Out of bounds";
			StackTraceElement tmp = Thread.currentThread().getStackTrace()[callStackDepth];
			return tmp.getClassName() + "#" + tmp.getMethodName() + "():" + tmp.getLineNumber();
		}

		public String getMethodName() {
			return "Current Thread StackTrace";
		}
	}

	/**
	 * Get a stack trace from a new Throwable
	 */
	private static class ThrowableStackTraceMethod implements GetCallerClassNameMethod {

		public String getCallerClassName(int callStackDepth) {
			if (new Throwable().getStackTrace().length <= callStackDepth)
				return "Out of bounds";
			StackTraceElement tmp = new Throwable().getStackTrace()[callStackDepth];
			return tmp.getClassName() + "#" + tmp.getMethodName() + "():" + tmp.getLineNumber();
		}

		public String getMethodName() {
			return "Throwable StackTrace";
		}
	}

	/**
	 * Use the SecurityManager.getClassContext()
	 */
	private static class SecurityManagerMethod implements GetCallerClassNameMethod {
		public String getCallerClassName(int callStackDepth) {
			return mySecurityManager.getCallerClassName(callStackDepth);
		}

		public String getMethodName() {
			return "SecurityManager";
		}

		/**
		 * A custom security manager that exposes the getClassContext() information
		 */
		static class MySecurityManager extends SecurityManager {
			public String getCallerClassName(int callStackDepth) {
				if (getClassContext().length <= callStackDepth)
					return "Out of bounds";
				return getClassContext()[callStackDepth].getName();
			}
		}

		private final static MySecurityManager mySecurityManager = new MySecurityManager();
	}

}