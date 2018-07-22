package com.extron.network.api.utils.tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private List<ExtronTask> allTasks = new ArrayList<>();
    private List<ExtronTask> delayedTasks = new ArrayList<>();
    private List<ExtronTask> repeatedTasks = new ArrayList<>();
    private List<ExtronTask> timedTasks = new ArrayList<>();
    private List<ExtronTask> cancelled = new ArrayList<>();
    private List<ExtronTask> add = new ArrayList<>();

    public void tick() {
        for (ExtronTask task : add) {
            allTasks.add(task);
            switch (task.getType()) {
                case DELAYED:
                    delayedTasks.add(task);
                    break;
                case REPEAT:
                    repeatedTasks.add(task);
                    break;
                case TIMER:
                    timedTasks.add(task);
                    break;
            }
        }
        add.clear();
        List<ExtronTask> remove = new ArrayList<>();
        for (ExtronTask t : delayedTasks) {
            if (t.time <= 0) {
                t.getRunnable().run();
                remove.add(t);
            } else {
                t.time--;
            }
        }
        for (ExtronTask t : timedTasks) {
            if (t.time <= 0) {
                t.getRunnable().run();
                t.time = t.getPeriod();
            } else {
                t.time--;
            }
        }
        for (ExtronTask t : repeatedTasks) {
            if (t.time <= 0) {
                t.getRunnable().run();
                t.time = t.getPeriod();
                t.count--;
                if (t.count <= 0) {
                    remove.add(t);
                }
            } else {
                t.time--;
            }
        }
        for (ExtronTask t : remove) {
            this.cancelTask(t.getRunnable());
        }
        for (ExtronTask task : cancelled) {
            allTasks.remove(task);
            switch (task.getType()) {
                case DELAYED:
                    delayedTasks.remove(task);
                    break;
                case REPEAT:
                    repeatedTasks.remove(task);
                    break;
                case TIMER:
                    timedTasks.remove(task);
                    break;
            }
        }
        cancelled.clear();
    }

    public void repeatTask(ExtronTask task) {
        repeatedTasks.add(task);
        allTasks.add(task);
    }

    public void timerTask(ExtronTask task) {
        timedTasks.add(task);
        allTasks.add(task);
    }

    public void delayTask(ExtronTask task) {
        delayedTasks.add(task);
        allTasks.add(task);
    }

    public void cancelTask(ExtronRunnable runnable) {
        ExtronTask remove = null;
        for (ExtronTask task : allTasks) {
            if (task.getRunnable().equals(runnable)) {
                remove = task;
            }
        }
        if (remove != null) {
            cancelled.add(remove);
        }
        runnable.onCancel();
    }

    public void addTask(ExtronTask task) {
        add.add(task);
    }
}
