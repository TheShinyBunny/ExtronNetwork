package com.extron.network.api.command;

import com.extron.network.api.command.exceptions.CommandFail;
import com.extron.network.api.utils.ListUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ParameterDispatcher<T> {

    private List<String> splitters = new ArrayList<>();
    private String separator;
    private T obj;
    private final Builder<T> builder;
    private Map<String,String> params;
    private List<Key<T>> keys;
    private DefaultKey<T> def;
    private Escape escapedSeparation;

    private ParameterDispatcher(Builder<T> builder, T storage) {
        this.builder = builder;
        this.params = new HashMap<>();
        this.keys = new ArrayList<>();
        this.obj = storage;
    }

    public T dispatch(String text) throws Exception {
        String[] entries = escapedSeparation == null ? text.split(separator) : escapedSeparation.split(text,separator);
        for (String e : entries) {
            boolean found = false;
            for (String split : splitters) {
                if (e.contains(split)) {
                    String[] splitted = e.split(split);
                    if (splitted.length == 2) {
                        params.put(splitted[0],splitted[1]);
                        found = true;
                        break;
                    } else {
                        throw new CommandFail("Parameter split of '" + e + "' is invalid!");
                    }
                }
            }
            if (!found) {
                for (Key k : keys) {
                    if (!k.isDefault() && !k.hasValue) {
                        if (k.name.equalsIgnoreCase(e)) {
                            params.put(k.name, null);
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (!found) {
                params.put(null, e);
            }
        }
        return this.process();
    }

    private T process() throws Exception {
        for (Map.Entry<String,String> e : params.entrySet()) {
            if (e.getKey() == null) {
                if (def.processor != null)
                    def.processor.accept(e.getValue(),obj);
            } else if (e.getValue() == null) {
                Key<T> k = this.getKey(e.getKey());
                if (k != null && k.keyPresent != null)
                    k.keyPresent.accept(obj);
            } else {
                Key<T> k = this.getKey(e.getKey());
                if (k != null) {
                    if (k.keyPresent != null)
                        k.keyPresent.accept(obj);
                    if (k.exception != null) {
                        Exception ex = k.exception.apply(e.getValue(),obj);
                        if (ex != null) {
                            throw new CommandFail(ex.getMessage());
                        }
                    }
                    if (k.processor != null)
                        k.processor.accept(e.getValue(),obj);
                }
            }
        }
        return obj;
    }

    public static <T> BiFunction<String,T,Exception> dispatchList(final String separator, final BiConsumer<T,List<String>> processor) {
        return (s, t) -> {
            System.out.println("lore is '" + s + "'");
            if (s.startsWith("[") && s.endsWith("]")) {
                s = s.substring(1,s.length()-1);
                String[] l = s.split(String.format("\\%s",separator));
                List<String> list = Arrays.asList(l);
                if (processor != null) {
                    processor.accept(t, list);
                }
                return null;
            }
            return new CommandFail("List \"" + s + "\" must be between square parentheses ( [ , ] ).");
        };
    }

    public static <T> BiFunction<String,T,Exception> dispatchListException(final String separator, final BiFunction<T,List<String>,Exception> exceptionProcessor) {
        return (s, t) -> {
            if (s.startsWith("[") && s.endsWith("]")) {
                String[] list = s.split(separator);
                if (exceptionProcessor != null) {
                    return exceptionProcessor.apply(t, Arrays.asList(list));
                }
                return null;
            }
            return new Exception("List \"" + s + "\" must be between square parentheses ( [ , ] ).");
        };
    }

    private Key<T> getKey(String name) {
        return ListUtils.firstMatch(keys, k->k.name.equalsIgnoreCase(name));
    }

    public static class Builder<T> {

        private ParameterDispatcher<T> dispatcher;

        public Builder(T storage) {
            dispatcher = new ParameterDispatcher<>(this,storage);
        }

        public Builder<T> addSplitter(String splitter) {
            dispatcher.splitters.add(splitter);
            return this;
        }

        public Builder<T> setSeparator(String separator) {
            dispatcher.separator = separator;
            return this;
        }

        public Builder<T> setSeparator(String separator, Escape escape) {
            dispatcher.separator = separator;
            dispatcher.escapedSeparation = escape;
            return this;
        }

        public Key<T> createKey(String name) {
            return new Key<>(dispatcher,name);
        }

        public DefaultKey<T> createDefaultKey() {
            return new DefaultKey<>(dispatcher);
        }

        public ParameterDispatcher<T> build() {
            return dispatcher;
        }
    }

    public static class Key<T> {

        protected final String name;
        protected final ParameterDispatcher<T> dispatcher;
        protected List<String> aliases;
        protected BiConsumer<String, T> processor;
        protected Consumer<T> keyPresent;
        protected boolean hasValue;
        protected BiFunction<String, T, Exception> exception;

        public Key(ParameterDispatcher<T> dispatcher, String name) {
            this.dispatcher = dispatcher;
            this.name = name;
            this.hasValue = true;
        }

        public Key<T> setHasNoValue() {
            this.hasValue = false;
            return this;
        }

        protected boolean isDefault() {
            return false;
        }

        public Key<T> setAliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Key<T> setAliases(String... aliases) {
            this.aliases = Arrays.asList(aliases);
            return this;
        }

        public Key<T> setValueProcessor(BiConsumer<String,T> processor) {
            this.processor = processor;
            return this;
        }

        public Key<T> setValueProcessorException(BiFunction<String,T,Exception> processorException) {
            this.exception = processorException;
            return this;
        }

        public Key<T> ifKeyPresent(Consumer<T> consumer) {
            this.keyPresent = consumer;
            return this;
        }

        public Builder<T> create() {
            this.dispatcher.keys.add(this);
            return this.dispatcher.builder;
        }

    }

    public static class DefaultKey<T> extends Key<T> {
        public DefaultKey(ParameterDispatcher<T> dispatcher) {
            super(dispatcher,"any");
            this.setHasNoValue();
        }

        @Override
        public Key<T> setAliases(List<String> aliases) {
            return this;
        }

        @Override
        public Key<T> setAliases(String... aliases) {
            return this;
        }

        @Override
        public Key<T> ifKeyPresent(Consumer<T> consumer) {
            return this;
        }

        @Override
        public Builder<T> create() {
            this.dispatcher.def = this;
            return this.dispatcher.builder;
        }

        @Override
        protected boolean isDefault() {
            return true;
        }
    }

    public static class Escape {

        private final String esc;

        private Escape(String esc) {
            this.esc = esc;
        }

        public static Escape between(String esc) {
            return new Escape(esc);
        }

        public static Escape quote() {
            return new Escape("\"");
        }

        public String[] split(String text, String separator) {
            return text.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)".replace(",",separator).replace("\"",esc));
        }
    }

}
