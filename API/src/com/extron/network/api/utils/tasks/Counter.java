package com.extron.network.api.utils.tasks;

public class Counter implements ExtronRunnable {

    private final int max;
    private final int dir;
    private final int period;
    private final Object attached;
    private final String id;
    private int current;
    private CounterAction handler;

    public Counter(int max, int dir, int period, String id, Object attachment) {
        this.id = id;
        this.max = max;
        this.dir = dir; // positive for counting up or negative for counting down
        this.period = period;
        this.current = dir > 0 ? 0 : max;
        this.attached = attachment;
    }

    @Override
    public void run() {
        if ((dir > 0 && current >= max) || (dir < 0 && current <= 0)) {
            this.cancel();
            this.handler.onCounterFinished(this);
        } else {
            this.handler.onCounterLoop(this);
        }
        current += dir;
    }

    public void start(CounterAction handler) {
        this.handler = handler;
        this.current = dir > 0 ? 0 : max;
        this.timer(0, period);
    }

    public int getDirection() {
        return dir;
    }

    public int getCurrent() {
        return current;
    }

    public CounterAction getHandler() {
        return handler;
    }

    public int getMax() {
        return max;
    }

    public void stop() {
        this.cancel();
        this.handler.onCounterStopped(this);
    }

    public Object getAttached() {
        return attached;
    }

    public boolean hasAttachment() {
        return attached != null;
    }

    public String getId() {
        return id;
    }

    public void skipTo(int n) {
        this.current = n;
    }
}
