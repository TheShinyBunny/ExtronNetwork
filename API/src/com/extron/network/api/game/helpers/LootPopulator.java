package com.extron.network.api.game.helpers;

import java.util.function.Consumer;

public interface LootPopulator {

    void populate(Consumer<LootEntry> c);

}
