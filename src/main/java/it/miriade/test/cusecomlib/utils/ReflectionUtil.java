package it.miriade.test.cusecomlib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security manager usa getClassContext() per recuperare il chiamante del metodo
 */
public class ReflectionUtil extends SecurityManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Il metodo ritorna la classe del primo oggetto che, percorrendo lo stack delle chiamate, è, estende o implementa
	 * la classe passata in input. Per percorrere lo stack delle chiamate viene usato
	 * {@link SecurityManager#getClassContext()}.
	 * 
	 * @param superClass
	 * @return
	 */
	public Class<?> getCaller(Class<?> superClass) {
		log.trace("Inspecting call hierarchy for a subclass of " + superClass + "...");
		for (Class<?> clazz : this.getClassContext()) {
			if (superClass == null || superClass.isAssignableFrom(clazz)) {
				log.trace("Found " + clazz);
				return clazz;
			}
		}
		throw new RuntimeException("Subclass of " + superClass + " not found");
	}

	/*
	 * Static methods
	 */

	private final static Logger slog = LoggerFactory.getLogger(ReflectionUtil.class);

	/**
	 * Il metodo ritorna lo {@link StackTraceElement} corrispondente al primo oggetto che, percorrendo lo stack delle
	 * chiamate, è, estende o implementa la classe passata in input. Per percorrere lo stack delle chiamate viene usato
	 * lo StackTrace del current thread.<br/>
	 * A differenza di {@link #getCaller(Class)} qui, tornando un {@link StackTraceElement}, abbiamo la possibilità di
	 * estrarre anche il metodo chiamante e la riga esatta del codice.
	 * 
	 * @param superClass
	 * @return
	 */
	public static StackTraceElement getStackElement(Class<?> superClass) {
		slog.trace("Inspecting call hierarchy for a subclass of " + superClass + "...");
		for (StackTraceElement ste : Thread.currentThread().getStackTrace())
			try {
				Class<?> clazz = Class.forName(ste.getClassName());
				if (superClass == null || superClass.isAssignableFrom(clazz)) {
					slog.trace("Found " + clazz);
					return ste;
				}
			} catch (Exception e) {
				slog.error(e.getMessage());
			}
		throw new RuntimeException("Subclass of " + superClass + " not found");
	}

	/*
	 * Formattazione degli StackTraceElement
	 */

	public static String templateClassAndMethod = "%s#%s";
	public static String templateClassMethodAndLineNumber = "%s#%s():%d";

	/**
	 * Formatta un singolo elemento dello stacktrace e torna il nome della classe e del metodo.
	 *
	 * @param el
	 * @return
	 * 		<code>${simpleClassName}#${methodName}()</code>
	 */
	public static String formatClassAndMethod(StackTraceElement el) {
		return String.format(templateClassAndMethod, el.getClassName().substring(el.getClassName().lastIndexOf('.') + 1), el.getMethodName());
	}

	/**
	 * Formatta un singolo elemento dello stacktrace e torna il nome della classe, il metodo e la linea di codice.
	 *
	 * @param el
	 * @return
	 * 		<code>${simpleClassName}#${methodName}():${lineNumber}</code>
	 */
	public static String formatClassMethodAndLineNumber(StackTraceElement el) {
		return String.format(templateClassMethodAndLineNumber, el.getClassName().substring(el.getClassName().lastIndexOf('.') + 1), el.getMethodName(), el.getLineNumber());
	}

	/*
	 * Metodi di inspect dello stacktrace
	 */

	static final List<String> excludeFromStack = Arrays.asList(Thread.class.getName(), ReflectionUtil.class.getName());

	/**
	 * <strong>SOLO PER DEVELOPER</strong><br/>
	 * Ritorna tutta la stacktrace delle chiamate partendo dall'oggeto chiamante.
	 * 
	 * @return stacktrace delle chiamate
	 */
	public static List<StackTraceElement> getStackTrace() {
		List<StackTraceElement> stack = new ArrayList<StackTraceElement>();
		Arrays.stream(Thread.currentThread().getStackTrace()).filter(el -> !excludeFromStack.contains(el.getClassName())).forEach(el -> {
			stack.add(el);
		});
		return stack;
	}

	/**
	 * <strong>SOLO PER DEVELOPER</strong><br/>
	 * Ritorna tutta la stacktrace delle chiamate applicando la formattazione di
	 * {@link #formatClassMethodAndLineNumber(StackTraceElement)}
	 * 
	 * @return stacktrace delle chiamate formattata
	 */
	// public static String getFormattedStackTrace() {
	// StringBuffer buf = new StringBuffer();
	// Arrays.stream(Thread.currentThread().getStackTrace()).filter(el ->
	// !excludeFromStack.contains(el.getClassName())).forEach(el -> {
	// buf.append("\t" + ReflectionUtil.formatClassMethodAndLineNumber(el) + "\n");
	// });
	// return buf.toString();
	// }

}
