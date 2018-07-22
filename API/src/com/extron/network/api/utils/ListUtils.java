package com.extron.network.api.utils;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListUtils {

    /**
     * Filters out from a list specific items, and puts all valid items in a new list.
     * @param list The list of <code>T</code> to filter from
     * @param filter The {@link Predicate filter} used to determine if an item is valid for the filtered list or not.
     * @param <T> The type of objects in the list.
     * @return A new list only with the item which returned <code>true</code> when tested with the {@link Predicate#test(Object) filter}.
     */
    public static <T> List<T> filter(Iterable<T> list, Predicate<T> filter) {
        List<T> newList = new ArrayList<>();
        for (T obj : list) {
            if (filter.test(obj)) {
                newList.add(obj);
            }
        }
        return newList;
    }

    /**
     * Whether the given {@link Iterable Iterable of Strings} contains the specified string, while ignoring case.
     * @param col The iterable of Strings to iterate on.
     * @param str The string to find.
     * @return Whether that string {@link String#equalsIgnoreCase(String) equals ignoring case} to another string in the Iterable.
     */
    public static boolean containsIgnoreCase(Iterable<String> col, String str) {
        for (String s : col) {
            if (s.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters out from a list all items approved by the filter (using {@link #filter(Iterable, Predicate) this method})<br/>
     * and returns the first object found matching.
     * @param list The iterable to search in
     * @param filter The filter {@link Predicate}
     * @param <T> The type of object
     * @return The first match of the filter, or null if no one matches.
     */
    public static <T> T firstMatch(Iterable<T> list, Predicate<T> filter) {
        List<T> l = filter(list,filter);
        return l.size() > 0 ? l.get(0) : null;
    }

    /**
     * Filters out from an array all items approved by the filter,
     * and returns the first object found matching.
     * @param arr The array to search in
     * @param filter The filter {@link Predicate}
     * @param <T> The type of object
     * @return The first match of the filter, or null if no one matches.
     */
    public static <T> T firstMatch(T[] arr, Predicate<T> filter) {
        for (T obj : arr) {
            if (filter.test(obj)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns the first index of the item matching by filtering them from the given list.
     * @param list The iterable to search on
     * @param filter The filter {@link Predicate}
     * @param <T> The type of object in the list
     * @return The 0-based index of the found item, or -1 if not found.
     */
    public static <T> int firstIndex(Iterable<T> list, Predicate<T> filter) {
        int i = 0;
        for (T obj : list) {
            if (filter.test(obj)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Converts all objects in a list to the result type using the given {@link Function converter}.
     * @param list The list
     * @param converter The converting {@link Function} when the input is the type of the input list and the output is the result's type.
     * @param <T> The type of input list
     * @param <R> The type of the result {@link Collection}.
     * @return A new Collection containing all converted items using the converter function.
     */
    public static <T,R> Collection<R> convertAll(Iterable<T> list, Function<T,R> converter) {
        Collection<R> newList = new ArrayList<>();
        for (T obj : list) {
            newList.add(converter.apply(obj));
        }
        return newList;
    }

    /**
     * Converts a {@link Supplier supply} of collection to a new type of collection supplier.
     * @param listSupply The supply of the collection
     * @param converter The function to convert the items.
     * @param <T> The type of input collection.
     * @param <R> The type of result supply of collection.
     * @return A new {@link Supplier} of a collection
     */
    public static <T,R> Supplier<Collection<R>> convertAllSupply(Supplier<Collection<T>> listSupply, Function<T,R> converter) {
       return () -> {
                Iterable<T> iter = listSupply.get();
                Collection<R> newList = new ArrayList<>();
                for (T t : iter) {
                    newList.add(converter.apply(t));
                }
                return newList;
            };
    }

    /**
     * Converts all collection to a collection of strings, using the standard {@link #toString()} method.
     * @param list The list to convert
     * @return A new list of strings representing the objects of the original list.
     */
    public static Collection<String> toStringAll(Collection<?> list) {
        Collection<String> newList = new ArrayList<>();
        for (Object o : list) {
            newList.add(o.toString());
        }
        return newList;
    }

    /**
     * Converts an array of items into a collections of different type of items, by converting them using the given function.
     * @param arr The array
     * @param converter The converter {@link Function}.
     * @param <T> The type of the original array
     * @param <R> The type of the result collection
     * @return A new Collection containing all converted items.
     */
    public static <T,R> Collection<R> convertAllArray(T[] arr, Function<T,R> converter) {
        Collection<R> newList = new ArrayList<>();
        for (T obj : arr) {
            newList.add(converter.apply(obj));
        }
        return newList;
    }

    /**
     * Returns a copy array starting from the given index up to the end of the given array.<br/>
     * An equivalent of:
     * <pre>
     *     Arrays.copyOfRange(arr,startIndex,arr.length);
     * </pre>
     * @param arr The input array
     * @param startIndex The start index
     * @param <T> The type of array
     * @return A new sub array starting from <code>startIndex</code> to arr.length.
     */
    public static <T> T[] subArray(T[] arr, int startIndex) {
        return Arrays.copyOfRange(arr,startIndex,arr.length);
    }

    /**
     * Returns a copy array starting from the given index up to the end index of the given array.<br/>
     * An equivalent of:
     * <pre>
     *     Arrays.copyOfRange(arr,startIndex,endIndex);
     * </pre>
     * @param arr The input array
     * @param startIndex The start index
     * @param endIndex The end index
     * @param <T> The type of array
     * @return A new sub array starting from <code>startIndex</code> to <code>endIndex</code>
     */
    public static <T> T[] subArray(T[] arr, int startIndex, int endIndex) {
        return Arrays.copyOfRange(arr,startIndex,endIndex);
    }


    public static <T> Collection<String> lowerCaseStringAll(Iterable<T> list) {
        Collection<String> newList = new ArrayList<>();
        for (T s : list) {
            newList.add(s.toString().toLowerCase());
        }
        return newList;
    }

    public static <T> Collection<String> lowerCaseStringAll(T[] arr) {
        Collection<String> newList = new ArrayList<>();
        for (T s : arr) {
            newList.add(s.toString().toLowerCase());
        }
        return newList;
    }

    /**
     * An equivalent of {@link Collection#contains(Object)}, but for arrays.
     * @param arr The array
     * @param obj The object to test if is found inside the array
     * @param <T> The type of objects in the array
     * @return Whether the given object is {@link #equals(Object) equal} to another object in the array.
     */
    public static <T> boolean arrayContains(T[] arr, T obj) {
        for (T t : arr) {
            if (t != null && obj != null) {
                if (t.equals(obj)) {
                    return true;
                }
            } else if (t == obj) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts an array to a supply of a collection containing the items of the array.
     * Equivalent of:
     * <pre>
     *     ()->Arrays.asList(arr);
     * </pre>
     * @param arr The array
     * @param <T> The type of objects
     * @return A new {@link Supplier} returning a list of the items.
     */
    public static <T> Supplier<Collection<T>> toSupply(T[] arr) {
        return ()->Arrays.asList(arr);
    }

    /**
     * Converts an collection to a supply of the collection.
     * Equivalent of:
     * <pre>
     *     ()->list;
     * </pre>
     * @param list The collection
     * @param <T> The type of objects
     * @return A new {@link Supplier} returning the collection
     */
    public static <T> Supplier<Collection<T>> toSupply(Collection<T> list) {
        return ()->list;
    }

    /**
     * Does 2 things:
     * <ol>
     *     <li>Converts the given collection to a supply</li>
     *     <li>Convert all items of that supply to a new supply of the result type</li>
     * </ol>
     * @param arr
     * @param converter
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> Supplier<Collection<R>> convertAndSupply(Collection<T> arr,Function<T,R> converter) {
        Supplier<Collection<T>> s = toSupply(arr);
        return convertAllSupply(s,converter);
    }

    public static <T> void printArray(T[] args) {
        List<T> list = Arrays.asList(args);
        System.out.println(list);
    }

    public static <T> List<T> modifyAll(List<T> list, Function<T,T> func) {
        List<T> newList = new ArrayList<>();
        for (T t : list) {
            newList.add(func.apply(t));
        }
        return newList;
    }

    /**
     * Returns a random item from the list.
     * @param list The list
     * @param <T> The type of items
     * @return A random item from the list. Won't be null unless was null inside the list or the list was empty.
     */
    public static <T> T randomItem(List<T> list) {
        return randomItem(list,FunctionUtils.alwaysTrue());
    }

    /**
     * Returns a random item from the list after filtering it with {@link #filter(Iterable, Predicate) this method}
     * @param list The list
     * @param filter The filter to test the items and take a random item only from the items where returned true by the {@link Predicate}
     * @param <T> The type of items
     * @return A random item from the list. Won't be null unless the list was empty.
     */
    public static <T> T randomItem(List<T> list, Predicate<T> filter) {
        List<T> newList = filter(list,filter);
        if (newList.isEmpty()) return null;
        if (newList.size() == 1) return newList.get(0);
        return newList.get(new Random().nextInt(newList.size()));
    }

    /**
     * Returns a random item from a list, using the WeightedRandomness algorithm.
     * @param list The list of items to choose from randomly
     * @param weightGetter The function to get from each item its weight (the chance of it being chosen)
     * @param <T> The type of items
     * @return A random item from the list. Won't be null unless the list was empty.
     */
    public static <T> T weightedRandomItem(List<T> list, Function<T,Integer> weightGetter) {
        if (list.isEmpty()) return null;
        int total = 0;
        for (T obj : list) {
            if (obj != null) {
                total += weightGetter.apply(obj);
            }
        }
        int index = -1;
        double rand = Math.random() * total;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) continue;
            rand -= weightGetter.apply(list.get(i));
            if (rand <= 0.0) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return null; // should never happen!
        }
        return list.get(index);
    }

    /**
     * Returns a list containing all values of an enum class.
     * @param enumClass The enum class
     * @param <E> The type of the enum
     * @return A new list containing all {@link Class#getEnumConstants() enum constants} of the class.
     */
    public static <E extends Enum<E>> List<E> enumList(Class<E> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }

    public static <T,R extends T> List<R> castAll(List<T> list, Class<R> targetClass) {
        List<R> newList = new ArrayList<>();
        for (T obj : list) {
            newList.add(targetClass.cast(obj));
        }
        return newList;
    }

    public static <T> List<T> toList(Iterable<T> iter) {
        List<T> list = new ArrayList<>();
        for (T obj : iter) {
            list.add(obj);
        }
        return list;
    }

    public static <T> List<T> drainRandomly(List<T> list, int groupIndex, int maxGroups, int randomnessRange) {
        int count = list.size() / (maxGroups - groupIndex);
        Random r = new Random();
        count += r.nextInt(randomnessRange) - r.nextInt(randomnessRange);
        List<T> drained = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (!list.isEmpty()) {
                T obj = list.get(list.size()-1);
                drained.add(obj);
                list.remove(list.size()-1);
            }
        }
        return drained;
    }

    public static <T> int arrayIndexOf(T[] arr, T obj) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    public static <K,V> Set<K> filterKeysByValue(Map<K, V> map, V value) {
        Set<K> keys = new HashSet<>();
        for (Map.Entry<K,V> e : map.entrySet()) {
            if (e.getValue().equals(value)) {
                keys.add(e.getKey());
            }
        }
        return keys;
    }
}
