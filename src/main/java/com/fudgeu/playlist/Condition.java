package com.fudgeu.playlist;


public class Condition {
    private String id;
    private boolean isCategory;
    private Condition.ConditionCheck conditionCheck;

    public Condition(String id, boolean isCategory, Condition.ConditionCheck conditionCheck) {
        this.id = id;
        this.isCategory = isCategory;
        this.conditionCheck = conditionCheck;
    }

    public boolean checkCondition() {
        return this.conditionCheck.check();
    }

    public String getId() {
        return this.id;
    }

    public boolean isCategory() {
        return this.isCategory;
    }

    public interface ConditionCheck {
        boolean check();
    }
}
