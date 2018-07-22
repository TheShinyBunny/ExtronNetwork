package com.extron.network.api.config;

import com.extron.network.api.Main;
import com.extron.network.api.config.yaml.YamlHelper;
import com.extron.network.api.config.yaml.YamlParseException;
import com.extron.network.api.config.yaml.YamlParser;
import com.extron.network.api.utils.Savable;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config extends ConfigSection implements Savable<Reader> {

    private static final boolean UTF8_OVERRIDE;
    private static final boolean UTF_BIG;

    private final YamlParser parser;

    private static final Map<Class<?>,Function<Object,String>> stringConverters = new HashMap<>();

    static {
        byte[] testBytes = Base64Coder.decode("ICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX4NCg==");
        Charset defaultCharset = Charset.defaultCharset();
        String resultString = new String(testBytes, defaultCharset);
        boolean trueUTF = defaultCharset.name().contains("UTF");
        UTF8_OVERRIDE = !" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\r\n".equals(resultString) || defaultCharset.equals(Charset.forName("US-ASCII"));
        UTF_BIG = trueUTF && UTF8_OVERRIDE;
    }

    private File file;

    public Config(File file, String id, boolean load) {
        super(id,null);
        this.file = file;
        this.parser = new YamlParser(this);
        if (load) {
            this.load();
        }
    }

    public Config(String id) {
        this(new File(Main.INSTANCE.getDataFolder(),id + ".yml"),id,true);
    }

    public Config(String id, boolean load) {
        this(new File(Main.INSTANCE.getDataFolder(),id + ".yml"),id,load);
    }

    public File getFile() {
        return file;
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            this.load(new InputStreamReader(stream, UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(Reader reader) {
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            parser.parse(input.lines().collect(Collectors.toList()));
        } catch (IOException | YamlParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            Files.createParentDirs(file);
            String data = this.toString();

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset())) {
                writer.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String toString() {
        return YamlHelper.toString(this);
    }

    public static <T> void registerStringConverter(Class<T> cls, Function<T,String> converter) {
        stringConverters.put(cls, (Function<Object, String>) converter);
    }

    public static Object toString(Object value) {
        for (Map.Entry<Class<?>,Function<Object,String>> e : stringConverters.entrySet()) {
            if (e.getKey().isInstance(value)) {
                return e.getValue().apply(value);
            }
        }
        return value;
    }
}
