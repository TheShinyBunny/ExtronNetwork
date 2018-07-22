package com.extron.network.api.config.yaml;

import com.extron.network.api.config.Config;
import com.extron.network.api.config.ConfigSection;

import java.util.List;
import java.util.Map;

public class YamlHelper {

    public static String toString(ConfigSection section) {
        return toString(section,0);
    }

    public static String toString(ConfigSection section, int i) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String,Object> e : section.toMap().entrySet()) {
            if (e.getValue() instanceof ConfigSection) {
                b.append(space(i) + e.getKey() + ":\n");
                b.append(toString((ConfigSection) e.getValue(), i + 1));
            } else if (e.getValue() instanceof List) {
                b.append(space(i) + e.getKey() + ":\n");
                for (Object o : (List)e.getValue()) {
                    b.append(space(i) + "- " + encode(o) + "\n");
                }
            } else {
                b.append(space(i) + e.getKey() + ": " + encode(e.getValue()));
                b.append("\n");
            }
        }
        return b.toString();
    }

    private static Object encode(Object obj) {
        return Config.toString(obj);
    }

    private static String space(int indent) {
        String s = "";
        for (int x = 0; x < indent; x++) {
            s += "  ";
        }
        return s;
    }

}
