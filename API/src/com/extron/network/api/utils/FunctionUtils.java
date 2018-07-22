package com.extron.network.api.utils;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionUtils {
	
	public static <T> Consumer<T> emptyConsumer() {
		return (t)->{};
	}
	
	public static <T> Predicate<T> alwaysTrue() {
		return t->true;
	}
	
	public static <T> Predicate<T> alwaysFalse() {
		return t->false;
	}
	
	public static <T> Predicate<T> negate(Predicate<T> predicate) {
		return (t)->!predicate.test(t);
	}
	
	public static <A,B> BiPredicate<A, B> and(Predicate<A> first, Predicate<B> second) {
		return (a,b)->first.test(a) && second.test(b);
	}
	
	public static <A,B> BiPredicate<A, B> or(Predicate<A> first, Predicate<B> second) {
		return (a,b)->first.test(a) || second.test(b);
	}
	
	public static <T> Function<T, T> noChange() {
		return t->t;
	}
	
	
}
