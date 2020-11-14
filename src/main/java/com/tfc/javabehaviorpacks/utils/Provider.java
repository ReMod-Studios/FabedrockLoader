package com.tfc.javabehaviorpacks.utils;

@FunctionalInterface
public interface Provider<T> {
	static <A> Provider<A> of(A t) {
		return () -> t;
	}
	
	T get();
}
