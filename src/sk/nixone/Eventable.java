package sk.nixone;

import java.util.HashSet;
import java.util.Set;

public class Eventable<Listener, EventType> {
	public static interface Invoker<Listener, EventType> {
		public void invoke(Listener l, EventType event);
	}
	
	private Set<Listener> listeners = new HashSet<Listener>();
	private Invoker<Listener, EventType> invoker;
	
	public Eventable(Invoker<Listener, EventType> invoker) {
		this.invoker = invoker;
	}
	
	public synchronized void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public synchronized void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public synchronized void invoke(EventType event) {
		for(Listener listener : CollectionUtil.cloneForIteration(listeners)) {
			invoker.invoke(listener, event);
		}
	}
}
