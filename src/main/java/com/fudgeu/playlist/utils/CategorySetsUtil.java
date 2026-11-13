package com.fudgeu.playlist.utils;

import com.fudgeu.playlist.objects.CategorySet;
import java.util.ArrayList;
import java.util.Objects;

public class CategorySetsUtil {
    public static CategorySet getCategorySet(ArrayList<CategorySet> sets, String category) {
        for (CategorySet set : sets) {
            if (Objects.equals(set.getCategoryId(), category)) {
                return set;
            }
        }

        return null;
    }

    public static boolean removeCategory(ArrayList<CategorySet> sets, String category) {
        int i = 0;

        for (CategorySet set : sets) {
            if (Objects.equals(set.getCategoryId(), category)) {
                sets.remove(i);
                return true;
            }

            i++;
        }

        return false;
    }

    public static boolean removeSubcategory(ArrayList<CategorySet> sets, String category, String subcategory) {
        CategorySet set = getCategorySet(sets, category);
        if (set != null && set.hasSubcategory(subcategory)) {
            set.removeCondition(subcategory);
            return true;
        } else {
            return false;
        }
    }

    public static void conform(ArrayList<CategorySet> original, ArrayList<CategorySet> conformTo) {
        original.clear();
        original.addAll(conformTo);
    }

    public static boolean equals(ArrayList<CategorySet> list1, ArrayList<CategorySet> list2) {
        if (list1.size() != list2.size()) {
            return false;
        } else {
            for (CategorySet set1 : list1) {
                CategorySet set2 = getCategorySet(list2, set1.getCategoryId());
                if (set2 == null || !equals(set1, set2)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean equals(CategorySet set1, CategorySet set2) {
        if (!Objects.equals(set1.getCategoryId(), set2.getCategoryId())) {
            return false;
        } else {
            ArrayList<String> subcats1 = set1.getSubcategories();
            ArrayList<String> subcats2 = set2.getSubcategories();
            if (subcats1.size() != subcats2.size()) {
                return false;
            } else {
                for (String subcat : subcats1) {
                    if (!subcats2.contains(subcat)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}
