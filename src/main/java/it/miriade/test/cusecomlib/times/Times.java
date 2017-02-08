package it.miriade.test.cusecomlib.times;

import java.util.Map;

import it.miriade.test.cusecomlib.CuseUtil;

/**
 * Classe per incapsulare gli intervalli di tempo usati in {@link CuseUtil#polling(WhileTrueAction, Object...)} e
 * {@link CuseUtil#wait(String)}.
 * 
 * @author svaponi
 */
public class Times {

	public static final String DELAY = "delay";
	public static final String MAX_WAITING_TIME = "max_waiting_time";
	public static final String START_AFTER = "start_after";

	/**
	 * Se l'oggetto è in input ad un metodo wait() allora {@link #delay} è il tempo di attesa.<br/>
	 * Se l'oggetto è in input ad un metodo polling() allora {@link #delay} è l'intervallo tra una ripetizione e
	 * l'altra.
	 * 
	 * @see CuseUtil#wait(Times)
	 * @see CuseUtil#polling(WhileTrueAction, Object...)
	 */
	public final Long delay;

	/**
	 * Pempo massimo di attesa del polling
	 */
	public final Long maxWaitingTime;

	/**
	 * Tempo di attesa prima di iniziare il polling
	 */
	public final Long startAfter;

	public Times() {
		super();
		this.delay = null;
		this.maxWaitingTime = null;
		this.startAfter = null;
	}

	public Times(Number delay) {
		super();
		this.delay = Long.parseLong(delay.toString());
		this.maxWaitingTime = null;
		this.startAfter = null;
	}

	public Times(Number delay, Number maxWaitingTime) {
		super();
		this.delay = Long.parseLong(delay.toString());
		this.maxWaitingTime = Long.parseLong(maxWaitingTime.toString());
		this.startAfter = null;
	}

	public Times(Number delay, Number maxWaitingTime, Number startAfter) {
		super();
		this.delay = Long.parseLong(delay.toString());
		this.maxWaitingTime = Long.parseLong(maxWaitingTime.toString());
		this.startAfter = Long.parseLong(startAfter.toString());
	}

	/**
	 * Costruisce un {@link Times} con i dati in mappa. La mappa deve contenere le chiavi {@value #DELAY} e
	 * {@value #MAX_WAITING_TIME} e {@value #START_AFTER}
	 * rispettivamente con intervallo di attesa, tempo massimo e pausa iniziale.
	 * 
	 * @param map
	 */
	public Times(Map<String, ?> map) {
		super();
		if (map == null || !map.containsKey(DELAY))
			throw new IllegalArgumentException("Invalid hook Map");
		Number delay = (Number) map.get(DELAY);
		Number maxWaitingTime = (Number) map.get(MAX_WAITING_TIME);
		Number startAfter = (Number) map.get(START_AFTER);
		this.delay = delay == null ? null : Long.parseLong(delay.toString());
		this.maxWaitingTime = maxWaitingTime == null ? null : Long.parseLong(maxWaitingTime.toString());
		this.startAfter = startAfter == null ? null : Long.parseLong(startAfter.toString());
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getSimpleName() + ": { delay: " + delay);
		if (maxWaitingTime != null)
			buf.append(", maxWaitingTime: " + maxWaitingTime);
		if (startAfter != null)
			buf.append(", startAfter: " + startAfter);
		buf.append(" }");
		return buf.toString();
	}

}