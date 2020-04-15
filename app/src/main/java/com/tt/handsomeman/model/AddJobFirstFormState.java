package com.tt.handsomeman.model;

import androidx.annotation.Nullable;

public class AddJobFirstFormState {
    @Nullable
    private Integer budgetMinError;
    @Nullable
    private Integer budgetMaxError;
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer detailError;

    private boolean isDataValid;

    public AddJobFirstFormState(@Nullable Integer budgetMinError,
                                @Nullable Integer budgetMaxError,
                                @Nullable Integer titleError,
                                @Nullable Integer detailError) {
        this.budgetMinError = budgetMinError;
        this.budgetMaxError = budgetMaxError;
        this.titleError = titleError;
        this.detailError = detailError;
        this.isDataValid = false;
    }

    public AddJobFirstFormState(boolean isDataValid) {
        this.budgetMinError = null;
        this.budgetMaxError = null;
        this.titleError = null;
        this.detailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getBudgetMinError() {
        return budgetMinError;
    }

    @Nullable
    public Integer getBudgetMaxError() {
        return budgetMaxError;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    @Nullable
    public Integer getDetailError() {
        return detailError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
