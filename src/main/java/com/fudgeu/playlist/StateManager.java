package com.fudgeu.playlist;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class StateManager {
    private final Map<String, String> stringStates = new HashMap<>();
    private final Map<String, Integer> intStates = new HashMap<>();
    private final Map<String, ResourceLocation> idStates = new HashMap<>();
    private final Map<String, Boolean> boolStates = new HashMap<>();

    public void setStateString(String id, String value) {
        this.stringStates.put(id, value);
    }

    public String getStateString(String id) {
        return this.stringStates.get(id);
    }

    public void clearStateString(String id) {
        this.stringStates.remove(id);
    }

    public void setStateInt(String id, int value) {
        this.intStates.put(id, value);
    }

    public Integer getStateInt(String id) {
        return this.intStates.get(id);
    }

    public void clearStateInt(String id) {
        this.intStates.remove(id);
    }

    public void setStateIdentifier(String id, ResourceLocation identifier) {
        this.idStates.put(id, identifier);
    }

    public ResourceLocation getStateIdentifier(String id) {
        return this.idStates.get(id);
    }

    public void clearStateIdentifier(String id) {
        this.idStates.remove(id);
    }

    public void setStateBool(String id, boolean value) {
        this.boolStates.put(id, value);
    }

    public boolean getStateBool(String id) {
        return this.boolStates.get(id);
    }

    public void clearStateBool(String id) {
        this.boolStates.remove(id);
    }
}
