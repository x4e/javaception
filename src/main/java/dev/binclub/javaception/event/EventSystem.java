package dev.binclub.javaception.event;

import java.util.*;
import java.util.function.Consumer;

public class EventSystem {
	private Map<Class<?>, List<Consumer<?>>> subscribers;
	
	public EventSystem() {
		subscribers = new HashMap<>();
	}
	
	public <T, U extends Consumer<T>> U subscribe(Class<T> event, U subscriber) {
		List<Consumer<?>> subs = subscribers.get(event);
		if (subs == null) {
			subs = new ArrayList<>();
			subscribers.put(event, subs);
		}
		subs.add(subscriber);
		return subscriber;
	}
	
	public <T, U extends Consumer<T>> boolean unsubscribe(Class<T> event, U subscriber) {
		List<Consumer<?>> subs = subscribers.get(event);
		if (subs != null) {
			return subs.remove(subscriber);
		}
		return false;
	}
	
	public <T> T dispatch(T event) {
		Class clazz = event.getClass();
		
		while (clazz != null && clazz != Object.class) {
			List<Consumer<?>> subs = subscribers.get(clazz);
			if (subs != null) {
				for (Consumer<?> sub : subs) {
					((Consumer<T>) sub).accept(event);
				}
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return event;
	}
}
