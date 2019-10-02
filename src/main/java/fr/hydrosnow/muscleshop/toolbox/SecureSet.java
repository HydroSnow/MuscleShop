package fr.hydrosnow.muscleshop.toolbox;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SecureSet<T> implements Iterable<T> {
	private final Set<T> s;

	public SecureSet() {
		s = new HashSet<>();
	}

	public synchronized boolean add(final T g) {
		return s.add(g);
	}

	public synchronized boolean remove(final T g) {
		return s.remove(g);
	}

	public synchronized int size() {
		return s.size();
	}

	@Override
	public synchronized Iterator<T> iterator() {
		final Set<T> l = new HashSet<>();
		for (final T t : s)
			l.add(t);
		return l.iterator();
	}
}
