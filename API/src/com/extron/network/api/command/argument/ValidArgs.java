package com.extron.network.api.command.argument;

import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.CiPredicate;
import com.extron.network.api.utils.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidArgs<T> {

    private CiPredicate<T, T, T> threeCompare;
    private T a;
    private T b;
    private BiFunction<ExtronPlayer, ExecuteData, Collection<T>> executionGetter;
    private Supplier<Collection<T>> getter;
    private Function<ExtronPlayer, Collection<T>> senderGetter;
    private boolean lower;

    public ValidArgs(Supplier<Collection<T>> method) {
        this.getter = method;
    }

    public ValidArgs(Function<ExtronPlayer,Collection<T>> method) {
        this.senderGetter = method;
    }

    public ValidArgs(BiFunction<ExtronPlayer,ExecuteData,Collection<T>> method) {
        this.executionGetter = method;
    }

    public ValidArgs(CiPredicate<T,T,T> method, T a, T b) {
        this.threeCompare = method;
        this.a = a;
        this.b = b;
    }

    public static <T> ValidArgs<T> getter(Supplier<Collection<T>> method) {
        return new ValidArgs<>(method);
    }

    public static <T> ValidArgs<T> getBySender(Function<ExtronPlayer,Collection<T>> method) {
        return new ValidArgs<>(method);
    }

    public static <T> ValidArgs<T> getByExecution(BiFunction<ExtronPlayer,ExecuteData,Collection<T>> method) {
        return new ValidArgs<>(method);
    }

    public static <T> ValidArgs<T> all() {
        return ValidArgs.getter(ArrayList::new);
    }

    public static ValidArgs<Double> minMax(double min, double max) {
        return new ValidArgs<>((x, a, b) -> x >= a && x <= b, min, max);
    }

    public static <T extends Enum<T>> ValidArgs<T> fromEnum(Class<T> enumClass,Predicate<T> filter) {
        return new ValidArgs<>(()->ListUtils.filter(Arrays.asList(enumClass.getEnumConstants()),filter)).lowerCase();
    }

    private ValidArgs<T> lowerCase() {
        this.lower = true;
        return this;
    }

    public boolean isValid(ExtronPlayer s, ExecuteData data, T input) {
        if (this.threeCompare != null) {
            return threeCompare.test(input,a,b);
        } else {
            Collection<T> valid = getValid(s,data);
            if (valid.isEmpty()) return true;

            if (input instanceof String) {
                return ListUtils.containsIgnoreCase((Collection<String>) valid,(String)input);
            } else {
                return valid.contains(input);
            }
        }
    }

    public Collection<T> getValid(ExtronPlayer sender, ExecuteData data) {
        if (this.getter != null) {
            return getter.get();
        } else if (this.senderGetter != null) {
            return senderGetter.apply(sender);
        } else if (executionGetter != null) {
            return executionGetter.apply(sender,data);
        }
        return new ArrayList<>();
    }

    public boolean isLower() {
        return lower;
    }
}
