package com.tt.handsomeman.model;

import androidx.annotation.Nullable;

public class SignUpAddPayoutFormState {
    @Nullable
    private final Integer firstNameError;
    @Nullable
    private final Integer lastNameError;
    @Nullable
    private final Integer addressError;
    @Nullable
    private final Integer portalCodeError;
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer accountNumberError;
    @Nullable
    private final Integer accountRoutingError;
    @Nullable
    private final Integer birthdayError;

    private final boolean isDataValid;

    public SignUpAddPayoutFormState(@Nullable Integer firstNameError,
                                    @Nullable Integer lastNameError,
                                    @Nullable Integer addressError,
                                    @Nullable Integer portalCodeError,
                                    @Nullable Integer emailError,
                                    @Nullable Integer accountNumberError,
                                    @Nullable Integer accountRoutingError,
                                    @Nullable Integer birthdayError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.addressError = addressError;
        this.portalCodeError = portalCodeError;
        this.emailError = emailError;
        this.accountNumberError = accountNumberError;
        this.accountRoutingError = accountRoutingError;
        this.birthdayError = birthdayError;
        isDataValid = false;
    }

    public SignUpAddPayoutFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.addressError = null;
        this.portalCodeError = null;
        this.emailError = null;
        this.accountNumberError = null;
        this.accountRoutingError = null;
        this.birthdayError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    public Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    public Integer getAddressError() {
        return addressError;
    }

    @Nullable
    public Integer getPortalCodeError() {
        return portalCodeError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getAccountNumberError() {
        return accountNumberError;
    }

    @Nullable
    public Integer getAccountRoutingError() {
        return accountRoutingError;
    }

    @Nullable
    public Integer getBirthdayError() {
        return birthdayError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
