package com.extron.network.api.config.yaml;

import com.extron.network.api.config.Config;
import com.extron.network.api.config.ConfigSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlParser {

    private int lineCounter = 1;
    private String currentLine;
    private List<String> original;
    private Config config;
    private int currentIndent;
    private ConfigSection currentSection;
    private List currentList;
    private String listKey;

    public YamlParser(Config config) {
        this.config = config;
    }

    public void parse(List<String> lines) throws YamlParseException {
        config.clear();
        System.out.println("line count: " + lines.size());
        original = lines;
        currentSection = config;
        currentIndent = 0;
        for (String line : lines) {
            currentLine = line;
            parseLine(line);
            lineCounter++;
        }
        if (currentList != null) {
            currentSection.set(listKey, currentList);
        }
    }

    private void parseLine(String line) throws YamlParseException {
        int indent = countIndent(line);
        line = line.trim();
        if (line.isEmpty() || indent == -1) return;
        int colon = line.indexOf(':');
        if (colon == -1 || line.startsWith("-")) {
            parseListItem(line);
            return;
        }
        String key = line.substring(0,colon);
        String value;
        if (colon == line.length()-1) {
            value = "";
        } else {
            value = line.substring(colon+1).trim();
        }
        System.out.println("key = " + key + " value = '" +  value + "'");
        if (currentList != null) {
            currentSection.set(listKey, currentList);
            currentList = null;
            listKey = null;
        }
        if (indent < currentIndent) {
            fixIndent(indent);
        }
        if (value.isEmpty()) {
            if (lineCounter < original.size()) {
                String next = original.get(lineCounter);
                if (next.trim().startsWith("-")) {
                    currentList = new ArrayList();
                    listKey = key;
                } else {
                    ConfigSection section = new ConfigSection(key,currentSection);
                    currentSection.set(key,section);
                    currentSection = section;
                    currentIndent++;
                }
            } else {
                throw new YamlParseException("Start of a list or a section at the last line!");
            }
        } else {
            if (indent == currentIndent) {
                currentSection.set(key,parseValue(value));
            } else {
                throw new YamlParseException("Unexpected indentation of " + currentLine + " (line " + lineCounter + ")");
            }
        }
    }

    private void fixIndent(int indent) {
        while (currentSection.parent() != null) {
            currentSection = currentSection.parent();
            currentIndent--;
            if (currentIndent == indent) return;
        }
    }

    private void parseListItem(String line) throws YamlParseException {
        if (line.startsWith("-")) {
            if (currentList == null) {
                throw new YamlParseException("Expected list at line " + lineCounter + " but currently not inside a list.");
            }
            String value = line.substring(1).trim();
            currentList.add(parseValue(value));
        } else {
            throw new YamlParseException("Expected list sign (-) at the start of line " + lineCounter + " but is not present.");
        }
    }

    private int countIndent(String line) throws YamlParseException {
        int i = 0;
        for (char c : line.toCharArray()) {
            if (!Character.isSpaceChar(c)) {
                if (i == 0) return 0;
                if (i % 2 == 0) {
                    return i / 2;
                } else {
                    throw new YamlParseException("Indent must be even. at line " + lineCounter);
                }
            }
            i++;
        }
        return -1;
    }

    private Object parseValue(String s) throws YamlParseException {
        s = s.trim();
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e2) {
                try {
                    return Float.parseFloat(s);
                } catch (Exception e3) {
                    try {
                        return Byte.parseByte(s);
                    } catch (Exception e4) {
                        try {
                            return Long.parseLong(s);
                        } catch (Exception e5) {
                            try {
                                return Short.parseShort(s);
                            } catch (Exception ignored) {

                            }
                        }
                    }
                }
            }
        }
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(s);
        }
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1,s.length()-1);
            String[] arr = s.split(",");
            Object o = parseValue(arr[0]);
            List<Object> list = new ArrayList<>();
            for (String i : arr) {
                Object x = parseValue(i);
                if (o.getClass().isInstance(x)) {
                    list.add(x);
                } else {
                    throw new YamlParseException("Type dismatch in array '" + s + "' of class " + x.getClass().getSimpleName() + ".class (line " + lineCounter +")");
                }
            }
            return list;
        }
        if ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
            s = s.substring(1,s.length()-1);
        }
        return s;
    }

}
