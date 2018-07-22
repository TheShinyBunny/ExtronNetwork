package com.extron.network.api.utils.tasks;

import com.extron.network.api.Main;

public interface ExtronRunnable extends Runnable {

    default ExtronRunnable repeat(int delay, int period, int times) {
        Main.getTaskManager().addTask(new ExtronTask(this,TaskType.REPEAT,delay,period,times));
        return this;
    }

    default ExtronRunnable timer(int delay, int period) {
        Main.getTaskManager().addTask(new ExtronTask(this,TaskType.TIMER,delay,period));
        return this;
    }

    default ExtronRunnable delay(int delay) {
        Main.getTaskManager().addTask(new ExtronTask(this,TaskType.DELAYED,delay));
        return this;
    }

    default void cancel() {
        Main.getTaskManager().cancelTask(this);
    }

    default void onCancel() {

    }
}
