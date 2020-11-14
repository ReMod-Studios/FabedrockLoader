package com.tfc.javabehaviorpacks.utils;

public interface BiProvider<T, V> {
	static <A, B> BiProvider<A, B> of(A t, B v) {
		return new BiProvider<A, B>() {
			@Override
			public A getT() {
				return t;
			}
			
			@Override
			public B getV() {
				return v;
			}
		};
	}
	
	T getT();
	
	V getV();
}
