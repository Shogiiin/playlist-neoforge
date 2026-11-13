package com.fudgeu.playlist.fluxui;

public class State {
    private final Runnable reRender;

    public State(Runnable reRender) {
        this.reRender = reRender;
    }

    public void triggerReRender() {
        this.reRender.run();
    }
}
