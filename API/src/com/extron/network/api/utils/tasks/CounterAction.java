package com.extron.network.api.utils.tasks;

import com.extron.network.api.game.managers.GameManager;

/**
 * This interface is used to handle 3 events of a {@link Counter} object:
 * <br />
 * - counter loop<br />
 * - counter finished<br />
 * - counter stopped<br />
 * This interface is currently only implemented by {@link GameManager}, but made as an interface to be able to be used for more things.
 */
public interface CounterAction {
    default void onCounterLoop(Counter c) {

    }

    default void onCounterFinished(Counter cd) {

    }

    default void onCounterStopped(Counter c) {

    }
}
