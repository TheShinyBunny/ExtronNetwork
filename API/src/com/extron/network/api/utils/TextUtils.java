package com.extron.network.api.utils;

import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    /**
     * A static line for fancy chat alerts.
     */
	public static final String LINE = "==========================================";

    public final static TreeMap<Integer, String> romanSigns;

    static {
        romanSigns = new TreeMap<>();
        romanSigns.put(1000, "M");
        romanSigns.put(900, "CM");
        romanSigns.put(500, "D");
        romanSigns.put(400, "CD");
        romanSigns.put(100, "C");
        romanSigns.put(90, "XC");
        romanSigns.put(50, "L");
        romanSigns.put(40, "XL");
        romanSigns.put(10, "X");
        romanSigns.put(9, "IX");
        romanSigns.put(5, "V");
        romanSigns.put(4, "IV");
        romanSigns.put(1, "I");

    }

    public static String toRoman(int number) {
        int x = romanSigns.floorKey(number);
        if (number == x) {
            return romanSigns.get(number);
        }
        return romanSigns.get(x) + toRoman(number-x);
    }


    /**
     * Will automatically add commas in numbers. For example, 18728742 will return 18,728,742.
     * @param x The number to format
     * @return The number with commas
     */
    public static String numberComma(int x) {
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(x);
    }

    /**
     * Does basically what the name says.
     * @param list The list to check
     * @param s The string to find
     * @return Whether the list contains the given string, ignoring case sensitivity.
     */
    public static boolean containsIgnoreCase(Collection<String> list, String s) {
        for (String st : list) {
            if (st.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Automatically separate a single string to a list by lines of a maximum size <code>lineLength</code>.
     * @param text The full string
     * @param lineLength The maximum length of a new line
     * @return The separated lines
     */
    public static List<String> autoNewLine(String text, int lineLength) {
        List<String> list = new ArrayList<>();
        boolean flag = true;
        int i = 0;
        do {
            try {
                String sub = text.substring(i, i + lineLength);
                String sub2 = sub.substring(0, sub.lastIndexOf(" "));
                list.add(ChatColor.RESET + sub2);
                i += sub2.length() + 1;
            } catch (IndexOutOfBoundsException e) {
                String sub = text.substring(i, text.length());
                list.add(ChatColor.RESET + sub);
                flag = false;
            }
        } while (flag);
        return list;
    }

    /**
     * Will list in a string all names of all players in a list
     * @param players The players
     * @return All the player names in a string
     */
    public static String listNamesNicely(List<ExtronPlayer> players) {
        StringBuilder b = new StringBuilder();
        for (ExtronPlayer p : players) {
            b.append(p.getName()).append(", ");
        }
        b.delete(b.length() - 3, b.length());
        if (players.size() > 1) {
            b.replace(b.lastIndexOf(",") - 1, b.lastIndexOf(",") + 2, " and ");
        }
        return b.toString();
    }

    /**
     * Adds a position suffix for numbers. For example, 8 will give 8th, 23 -> 23rd, 911 -> 911st
     * @param x The number to format
     * @return The number string with suffix
     */
    public static String addPosSuffix(int x) {
        if (Integer.toString(x).endsWith("1")) {
            return x + "st";
        } else if (Integer.toString(x).endsWith("2")) {
            return x + "nd";
        } else if (Integer.toString(x).endsWith("3")) {
            return x + "rd";
        }
        return x + "th";
    }

    /**
     * Creates a clickable chat json message.
     * @param p The player to send the message to
     * @param click_here The text that will be clicked
     * @param command The command to run on click
     * @param suffix The string after the clicky text
     */
    public static void sendClickableMessage(ExtronPlayer p, String click_here, String command, String suffix) {
        p.sendJsonMessage(new JsonBuilder().formattedText(click_here).runCommand(command).hoverText("Run /" + command).formattedText(suffix).toString());
    }

    /**
     * Will calculate the time in the format of h:mm:ss:tt from an amount of ticks.
     * @param ticks The tick count
     * @return The ticks as a time string
     */
    public static String ticksToTime(int ticks) {
        int hours = ticks / 72000;
        ticks -= hours * 60 * 60 * 20;
        int minutes = ticks / 1200;
        ticks -= minutes * 60 * 20;
        int seconds = ticks / 20;
        ticks -= seconds * 20;
        String m0 = "";
        if (minutes < 10) {
            m0 = "0";
        }
        String s0 = "";
        if (seconds < 10) {
            s0 = "0";
        }
        String t0 = "";
        if (ticks < 10) {
            t0 = "0";
        }
        return hours + ":" + m0 + minutes + ":" + s0 + seconds + ":" + t0 + ticks;
    }

    /**
     * Will add a plural S if the specified number isn't 1.
     * @param i The number variable
     * @param s The singular word
     * @return The pluralized word as needed.
     */
    public static String addNeededS(int i, String s) {
        if (i == 1) {
            return s;
        }
        return Plurals.pluralize(s);
    }

    /**
     * Will shrink a string to the maximum length given.
     * @param text The string to shrink
     * @param max The maimum string length
     * @return The given string if max is larger or equal to the string length, or a substring from the start to index 'max'.
     */
    public static String shrinkToLength(String text, int max) {
        if (max >= text.length()) {
            return text;
        } else {
            return text.substring(0,max);
        }
    }

    /**
     * Will convert all object in a list to strings, using the {@link Object#toString()}
     * @param list The list to convert
     * @return a string list of the objects in the original list.
     */


    /**
     * Converts to upper case every letter on the start or after a period.
     * @param text
     * @return
     */
    public static String capitalize(String text) {
        if (!text.contains(".")) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        boolean hasLastPeriod = text.charAt(text.length()-1) == '.';
        if (hasLastPeriod) {
            text = text.substring(0,text.length()-1);
        }
        String[] sentences = text.split(".");
        StringBuilder newTextBuilder = new StringBuilder();
        for (String s : sentences) {
            String f = String.valueOf(s.charAt(0)).toUpperCase();
            String sen = f + s.substring(1);
            newTextBuilder.append(sen).append(".");
        }
        String newText = newTextBuilder.toString();
        if (hasLastPeriod) {
            newText += ".";
        }
        return newText;
    }


    public static boolean isAlphanumeric(String text) {
        return text.matches("^[a-zA-Z0-9_]*$");
    }

    public static boolean startsWithIgnoreCase(String text, String prefix) {
        return text.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public static <T extends Enum<T>> T getEnumValue(Class<T> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass,name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String pluralize(String word) {
        return Plurals.pluralize(word.toLowerCase());
    }

    public static String pluralize(String word, boolean capitalize) {
        if (capitalize) {
            return capitalize(pluralize(word));
        }
        return pluralize(word);
    }

    public enum ClickAction {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }
    public enum HoverAction {
        SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY;
    }

    public static class JsonBuilder {

        private List<TextSegment> segments;
        private TextSegment last;

        public JsonBuilder() {
            segments = new ArrayList<>();
        }

        public JsonBuilder addSegment(TextSegment segment) {
            segments.add(segment);
            last = segment;
            return this;
        }

        public JsonBuilder rawText(String text) {
            return this.addSegment(new TextSegment(text));
        }

        public JsonBuilder formattedText(String text) {
            for (TextSegment s : getJSON(text)) {
                this.addSegment(s);
            }
            return this;
        }

        public JsonBuilder setColor(ChatColor color) {
            if (last != null && color.isColor()) {
                last.color = color;
            }
            return this;
        }

        public JsonBuilder bold(boolean bold) {
            if (last != null) {
                last.bold = bold;
            }
            return this;
        }

        public JsonBuilder italic(boolean italic) {
            if (last != null) {
                last.italic = italic;
            }
            return this;
        }

        public JsonBuilder underline(boolean underline) {
            if (last != null) {
                last.underline = underline;
            }
            return this;
        }

        public JsonBuilder strikeThrough(boolean st) {
            if (last != null) {
                last.strikethrough = st;
            }
            return this;
        }

        public JsonBuilder obfuscated(boolean obfuscated) {
            if (last != null) {
                last.obfuscated = obfuscated;
            }
            return this;
        }

        public JsonBuilder clickAction(ClickAction action, String value) {
            if (last != null) {
                last.clickEvent = Pair.of(action,value);
            }
            return this;
        }

        public JsonBuilder hoverAction(HoverAction action, String value) {
            if (last != null) {
                last.hoverEvent = Pair.of(action,value);
            }
            return this;
        }

        public JsonBuilder runCommand(String cmd) {
            return clickAction(ClickAction.RUN_COMMAND,cmd);
        }

        public JsonBuilder hoverText(String text) {
            return hoverAction(HoverAction.SHOW_TEXT,text);
        }

        @Override
        public String toString() {
            if (segments.isEmpty()) {
                return "{}";
            }
            StringBuilder b = new StringBuilder();
            if (segments.size() > 1) {
                b.append("[");
            }
            for (TextSegment s : segments) {
                b.append(s.toString());
                if (s != last) {
                    b.append(",");
                }
            }
            if (segments.size() > 1) {
                b.append("]");
            }
            System.out.println("json text component: " + b.toString());
            return b.toString();
        }
    }

    public static class TextSegment {

        private String text;
        private ChatColor color;
        private boolean italic;
        private boolean bold;
        private boolean underline;
        private boolean strikethrough;
        private boolean obfuscated;
        private Pair<ClickAction,String> clickEvent;
        private Pair<HoverAction,String> hoverEvent;

        public TextSegment(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder("{");
            int i = 0;

            for (Field f : getClass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(this);
                    if (obj instanceof Boolean && (Boolean) obj) {
                        addValue(b,f,"true");
                    } else if (obj instanceof String) {
                        addValue(b,f,"\"" + obj + "\"");
                    } else if (obj instanceof Pair) {
                        addValue(b,f,"{\"action\":\"" + ((Pair) obj).getFirst().toString().toLowerCase() + "\",\"value\":\"" + ((Pair) obj).getSecond().toString() + "\"}");
                    } else if (obj instanceof ChatColor) {
                        addValue(b,f,nameOfColor((ChatColor) obj));
                    }
                } catch (Exception e) {

                }
                if (i < getClass().getDeclaredFields().length - 1) {
                    b.append(",");
                }
            }
            b.append("}");
            return b.toString();
        }

        private void addValue(StringBuilder b, Field f, String val) {
            b.append("\"" + f.getName() + "\":" + val);
        }
    }


    /**
     * Parse a json text message from a ChatColor-based message.
     * @param s
     * @return
     */
    public static List<TextSegment> getJSON(String s) {
        String regex = "[&§]([a-fA-Fk-oL-O0-9rR])";
        Pattern p = Pattern.compile(regex);
        if (!p.matcher(s).find()) {
            return Arrays.asList(new TextSegment(s));
        }
        s = s.replaceAll(regex,"§$1");
        List<TextSegment> list = new ArrayList<>();
        boolean sign = false;
        ChatColor last = null;
        String seg = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ChatColor.COLOR_CHAR) {
                sign = true;
                if (last != null) {
                    if (last.isColor()) {
                        TextSegment segment = new TextSegment(seg);
                        segment.color = last;
                        list.add(segment);
                    } else {
                        TextSegment segment = new TextSegment(seg);
                        Reflection.setValue(segment,nameOfColor(last),true);
                        list.add(segment);
                    }
                    seg = "";
                    last = null;
                }
            } else {
                if (sign && p.matcher(new String(new char[]{s.charAt(i-1),c})).matches()) {
                    last = ChatColor.getByChar(c);
                } else {
                    seg += c;
                }
                sign = false;
            }
        }
        if (last != null) {
            if (last.isColor()) {
                TextSegment segment = new TextSegment(seg);
                segment.color = last;
                list.add(segment);
            } else {
                TextSegment segment = new TextSegment(seg);
                Reflection.setValue(segment,nameOfColor(last),true);
                list.add(segment);
            }
        }
        System.out.println("list of segments:");
        list.forEach(System.out::println);
        return list;
    }

    public static String nameOfColor(ChatColor color) {
        return color == ChatColor.MAGIC ? "obfuscated" : color.name().toLowerCase();
    }

    public static class Plurals {
        // @formatter:off
        private static final List<String> unpluralizables = Arrays.asList(
                "equipment", "information", "rice", "money", "species", "series",
                "fish", "sheep", "deer");

        private static final List<Replacer> singularizations = Arrays.asList(
                replace("(.*)people$").with("$1person"),
                replace("oxen$").with("ox"),
                replace("children$").with("child"),
                replace("feet$").with("foot"),
                replace("teeth$").with("tooth"),
                replace("geese$").with("goose"),
                replace("(.*)ives?$").with("$1ife"),
                replace("(.*)ves?$").with("$1f"),
                replace("(.*)men$").with("$1man"),
                replace("(.+[aeiou])ys$").with("$1y"),
                replace("(.+[^aeiou])ies$").with("$1y"),
                replace("(.+)zes$").with("$1"),
                replace("([m|l])ice$").with("$1ouse"),
                replace("matrices$").with("matrix"),
                replace("indices$").with("index"),
                replace("(.+[^aeiou])ices$").with("$1ice"),
                replace("(.*)ices$").with("$1ex"),
                replace("(octop|vir)i$").with("$1us"),
                replace("(.+(s|x|sh|ch))es$").with("$1"),
                replace("(.+)s$").with("$1")
        );

        private static final List<Replacer> pluralizations = Arrays.asList(
                replace("(.*)person$").with("$1people"),
                replace("ox$").with("oxen"),
                replace("child$").with("children"),
                replace("foot$").with("feet"),
                replace("tooth$").with("teeth"),
                replace("goose$").with("geese"),
                replace("(.*)fe?$").with("$1ves"),
                replace("(.*)man$").with("$1men"),
                replace("(.+[aeiou]y)$").with("$1s"),
                replace("(.+[^aeiou])y$").with("$1ies"),
                replace("(.+z)$").with("$1zes"),
                replace("([m|l])ouse$").with("$1ice"),
                replace("(.+)(e|i)x$").with("$1ices"),
                replace("(octop|vir)us$").with("$1i"),
                replace("(.+(s|x|sh|ch))$").with("$1es"),
                replace("(.+)").with("$1s" )
        );
        // @formatter:on

        /**
         * If possible, ensure the provided word is a singular word form.
         *
         * @return The singular form of the word, or the input if no
         *         rules test.
         */
        public static String singularize(String word) {
            if (unpluralizables.contains(word.toLowerCase())) {
                return word;
            }

            for (final Replacer singularization : singularizations) {
                if (singularization.matches(word)) {
                    return singularization.replace();
                }
            }

            return word;
        }

        /**
         * If possible, ensure the provided word is a plural word form.
         *
         * @return The plural form of the word, or the input if no
         *         rules test.
         */
        public static String pluralize(String word) {
            if (unpluralizables.contains(word.toLowerCase())) {
                return word;
            }

            for (final Replacer pluralization : pluralizations) {
                if (pluralization.matches(word.toLowerCase())) {
                    return pluralization.replace();
                }
            }

            return word;
        }

        /**
         * A simple helper class with a Builder to provide a little syntactic sugar
         */
        static class Replacer {
            Pattern pattern;
            String replacement;
            Matcher m;

            static class Builder {
                private final Pattern pattern;

                Builder(Pattern pattern) {
                    this.pattern = pattern;
                }

                Replacer with(String replacement) {
                    return new Replacer(pattern, replacement);
                }
            }

            private Replacer(Pattern pattern, String replacement) {
                this.pattern = pattern;
                this.replacement = replacement;
            }

            boolean matches(String word) {
                m = pattern.matcher(word);
                return m.matches();
            }

            String replace() {
                return m.replaceFirst(replacement);
            }
        }

        static Replacer.Builder replace(String pattern) {
            return new Replacer.Builder(Pattern.compile(pattern));
        }
    }
}
