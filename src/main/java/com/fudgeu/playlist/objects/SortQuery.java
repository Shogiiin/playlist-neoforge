package com.fudgeu.playlist.objects;

import com.fudgeu.playlist.enums.SortCategory;
import com.fudgeu.playlist.enums.SortDirection;

public class SortQuery {
    private SortCategory sortCategory = SortCategory.TITLE;
    private SortDirection sortDirection = SortDirection.ASCENDING;

    public SortQuery(SortCategory category, SortDirection direction) {
        this.sortCategory = category;
        this.sortDirection = direction;
    }

    public SortCategory getCategory() {
        return this.sortCategory;
    }

    public SortDirection getDirection() {
        return this.sortDirection;
    }

    public void setSortCategory(SortCategory category) {
        this.sortCategory = category;
    }

    public void setSortDirection(SortDirection direction) {
        this.sortDirection = direction;
    }

    public void reverseSortDirection() {
        if (this.sortDirection == SortDirection.ASCENDING) {
            this.sortDirection = SortDirection.DESCENDING;
        } else {
            this.sortDirection = SortDirection.ASCENDING;
        }
    }
}
