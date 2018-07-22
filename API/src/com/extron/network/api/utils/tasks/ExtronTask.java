package com.extron.network.api.utils.tasks;

public class ExtronTask {
    private final ExtronRunnable runnable;
    private final TaskType type;
    private final int delay;
    private int period;
    public int count;
    public int time;

    public ExtronTask(ExtronRunnable runnable, TaskType type, int delay) {
        this.runnable = runnable;
        this.type = type;
        this.delay = delay;
        this.period = 0;
        this.count = 1;
        this.time = delay;
    }

    public ExtronTask(ExtronRunnable runnable, TaskType type, int delay, int period) {
        this(runnable, type, delay);
        this.period = period;
        this.count = 1;
    }

    public ExtronTask(ExtronRunnable runnable, TaskType type, int delay, int period, int times) {
        this(runnable, type, delay, period);
        this.count = times;
    }

    public ExtronRunnable getRunnable() {
        return runnable;
    }

    public TaskType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }
}
