package com.fudgeu.playlist.fluxui.transitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class TransitionHelper {
    private double currentTick = 0.0;
    private final HashMap<UUID, TransitionHelper.FluxTimer> timeoutTasks = new HashMap<>();

    public void tick(float delta) {
        this.currentTick += delta;
        HashMap<UUID, TransitionHelper.FluxTimer> timeoutTasksTemp = new HashMap<>(this.timeoutTasks);
        ArrayList<UUID> trashList = new ArrayList<>();

        for (UUID timerId : timeoutTasksTemp.keySet()) {
            TransitionHelper.FluxTimer timer = this.timeoutTasks.get(timerId);
            if (timer.whenToRun <= this.currentTick) {
                timer.toRun.run();
                trashList.add(timerId);
            }
        }

        for (UUID i : trashList) {
            this.timeoutTasks.remove(i);
        }
    }

    public UUID setTimeout(Runnable toRun, int ticks) {
        UUID uuid = UUID.randomUUID();
        this.timeoutTasks.put(uuid, new TransitionHelper.FluxTimer(ticks, toRun));
        return uuid;
    }

    public void resetTimeout(UUID timerId) {
        TransitionHelper.FluxTimer timer = this.timeoutTasks.get(timerId);
        if (timer != null) {
            timer.whenToRun = this.currentTick + timer.length;
        }
    }

    public void removeTimeout(UUID timerId) {
        this.timeoutTasks.remove(timerId);
    }

    public boolean timeoutExists(UUID timerId) {
        return this.timeoutTasks.containsKey(timerId);
    }

    public Supplier<Float> simpleLinear(int ticks) {
        double startTick = this.currentTick;
        return () -> startTick + ticks < this.currentTick ? 1.0F : (float)(this.currentTick - startTick) / ticks;
    }

    public Supplier<Integer> linear(int from, int to, int ticks) {
        int range = to - from;
        Supplier<Float> scalar = this.simpleLinear(ticks);
        return () -> from + Math.round(scalar.get() * range);
    }

    public Supplier<Float> linear(float from, float to, int ticks) {
        float range = to - from;
        Supplier<Float> scalar = this.simpleLinear(ticks);
        return () -> from + scalar.get() * range;
    }

    public Supplier<Integer> bezier(int from, int to, int ticks) {
        int range = to - from;
        Supplier<Float> scalar = this.simpleLinear(ticks);
        return () -> {
            float scalarVal = scalar.get();
            if (scalarVal >= 1.0F) {
                return to;
            } else {
                float bezier = scalarVal * scalarVal * (3.0F - 2.0F * scalarVal);
                int result = Math.round(bezier * range);
                return from + result;
            }
        };
    }

    public Supplier<Float> bezier(float from, float to, int ticks) {
        float range = to - from;
        Supplier<Float> scalar = this.simpleLinear(ticks);
        return () -> {
            float scalarVal = scalar.get();
            if (scalarVal >= 1.0F) {
                return to;
            } else {
                float bezier = scalarVal * scalarVal * (3.0F - 2.0F * scalarVal);
                float result = bezier * range;
                return from + result;
            }
        };
    }

    public Supplier<Float> easeIn(float from, float to, int ticks) {
        float range = to - from;
        Supplier<Float> scalar = this.simpleLinear(ticks);
        return () -> from + range * (float)Math.pow(scalar.get().floatValue(), 2.0);
    }

    public Supplier<Integer> easeIn(int from, int to, int ticks) {
        Supplier<Float> floatEaseIn = this.easeIn((float)from, (float)to, ticks);
        return () -> Math.round(floatEaseIn.get());
    }

    private class FluxTimer {
        public int length;
        public double whenToRun;
        public Runnable toRun;

        public FluxTimer(int length, Runnable toRun) {
            this.length = length;
            this.whenToRun = TransitionHelper.this.currentTick + length;
            this.toRun = toRun;
        }
    }
}
