package com.tt.handsomeman.model;

import androidx.annotation.Nullable;

public class SignUpFormState {
    @Nullable
    private final Integer nameError;
    @Nullable
    private final Integer mailError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer rePasswordError;

    private final boolean isDataValid;

    public SignUpFormState(@Nullable Integer nameError,
                           @Nullable Integer mailError,
                           @Nullable Integer passwordError,
                           @Nullable Integer rePasswordError) {
        this.nameError = nameError;
        this.mailError = mailError;
        this.passwordError = passwordError;
        this.rePasswordError = rePasswordError;
        this.isDataValid = false;
    }

    public SignUpFormState(boolean isDataValid) {
        this.nameError = null;
        this.mailError = null;
        this.passwordError = null;
        this.rePasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getMailError() {
        return mailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getRePasswordError() {
        return rePasswordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
