package com.extron.commands.arguments;

import com.extron.commands.ExecuteException;
import com.extron.commands.InvalidArgumentException;
import com.extron.commands.OfflineOnly;
import com.extron.commands.dispatcher.AnnotationAdapter;
import com.extron.commands.dispatcher.ArgumentAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OfflinePlayerAdapter implements ArgumentAdapter<OfflinePlayer>,AnnotationAdapter<OfflineOnly> {
    @Override
    public Class<OfflinePlayer> getType() {
        return OfflinePlayer.class;
    }

    @Override
    public OfflinePlayer parse(String input, String argName) throws InvalidArgumentException {
        OfflinePlayer p = Bukkit.getOfflinePlayer(input);
        if (p == null) {
            throw InvalidArgumentException.unknown(argName,input);
        }
        return p;
    }

    @Nullable
    @Override
    public OfflinePlayer defaultsTo() {
        return null;
    }

    @Override
    public Class<OfflineOnly> getAnnotationType() {
        return OfflineOnly.class;
    }

    @Override
    public boolean isValidParamType(Class<?> type) {
        return type == OfflinePlayer.class;
    }

    @Override
    public Object validate(Object obj, OfflineOnly offlineOnly, Class<?> usedType, String argName) throws ExecuteException {
        if (obj instanceof OfflinePlayer) {
            if (((OfflinePlayer) obj).isOnline()) {
                throw InvalidArgumentException.onlinePlayer(((OfflinePlayer) obj).getName());
            }
        }
        return obj;
    }

    @Override
    public Collection<String> getArgCompletions(Parameter p) {
        List<String> list = new ArrayList<>();
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (!p.isAnnotationPresent(OfflineOnly.class) || !op.isOnline()) {
                list.add(op.getName());
            }
        }
        return list;
    }
}
