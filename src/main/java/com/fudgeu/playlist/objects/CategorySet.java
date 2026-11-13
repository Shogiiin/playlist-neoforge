package com.fudgeu.playlist.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;

public class CategorySet {
    private final String categoryId;
    private ArrayList<String> subcategories;

    public CategorySet(String categoryId, ArrayList<String> subcategories) {
        this.categoryId = categoryId;
        this.subcategories = subcategories;
    }

    public boolean hasSubcategories() {
        return this.subcategories.size() > 0;
    }

    public boolean hasSubcategory(String subcategory) {
        return this.subcategories.contains(subcategory);
    }

    public void removeCondition(String condition) {
        this.subcategories.remove(condition);
    }

    public boolean addSubcategory(String conditionId) {
        if (this.subcategories.contains(conditionId)) {
            return false;
        } else {
            this.subcategories.add(conditionId);
            return true;
        }
    }

    public JsonObject serialize() {
        JsonObject set = new JsonObject();
        set.add("categoryId", JsonParser.parseString(this.categoryId));
        JsonArray conditionSet = new JsonArray();

        for (String condition : this.subcategories) {
            conditionSet.add(condition);
        }

        set.add("conditions", conditionSet);
        return set;
    }

    public static CategorySet deserialize(JsonObject object) {
        ArrayList<String> conditions = new ArrayList<>();

        for (JsonElement condition : object.get("conditions").getAsJsonArray()) {
            conditions.add(condition.getAsString());
        }

        return new CategorySet(object.get("categoryId").getAsString(), conditions);
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public ArrayList<String> getSubcategories() {
        return this.subcategories;
    }
}
