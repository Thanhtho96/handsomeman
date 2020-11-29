package com.tt.handsomeman.model;

import androidx.annotation.Nullable;

public class ChangePasswordFormState {
    @Nullable
    private final Integer currentPasswordError;
    @Nullable
    private final Integer newPasswordError;
    @Nullable
    private final Integer rePasswordError;

    private final boolean isDataValid;

    public ChangePasswordFormState(@Nullable Integer currentPasswordError,
                                   @Nullable Integer newPasswordError,
                                   @Nullable Integer rePasswordError,
                                   boolean isDataValid) {
        this.currentPasswordError = currentPasswordError;
        this.newPasswordError = newPasswordError;
        this.rePasswordError = rePasswordError;
        this.isDataValid = false;
    }

    public ChangePasswordFormState(boolean isDataValid) {
        this.currentPasswordError = null;
        this.newPasswordError = null;
        this.rePasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getCurrentPasswordError() {
        return currentPasswordError;
    }

    @Nullable
    public Integer getNewPasswordError() {
        return newPasswordError;
    }

    @Nullable
    public Integer getRePasswordError() {
        return rePasswordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
